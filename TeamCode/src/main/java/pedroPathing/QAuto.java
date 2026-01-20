package pedroPathing;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;
import static pedroPathing.QAuto.PathState.DONE;
import static pedroPathing.QAuto.PathState.DRIVE_INTAKE1;
import static pedroPathing.QAuto.PathState.DRIVE_INTAKE1_WAIT;
import static pedroPathing.QAuto.PathState.DRIVE_INTAKE2_X;
import static pedroPathing.QAuto.PathState.DRIVE_INTAKE3_X;
import static pedroPathing.QAuto.PathState.DRIVE_SHOOT2;
import static pedroPathing.QAuto.PathState.SHOOT1_WAIT;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;

import global.Initializer;
import robotparts.hardware.Turret;

import static robotparts.RobotConfig.intake;
import static robotparts.RobotConfig.turret;
import static teleop.TeleChain.Intake;
import static teleop.TeleChain.ShootAuto;
import static teleop.opmodes.QbitOp.isTurret23Mode;
import static teleop.opmodes.QbitOp.isTurretTargeting;
import static teleop.opmodes.QbitOp.oldTarget;
import static teleop.opmodes.QbitOp.shooterTarget;
import static teleop.opmodes.QbitOp.turnError;

@Autonomous(name = "QAuto", group = "Autonomous")
@Configurable
public class QAuto extends OpMode implements Initializer {
    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private PathState pathState;
    private Paths paths;
    private Timer pathTimer, opModeTimer;

    @Override
    public void init() {
        _init(this);
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        pathState = PathState.DRIVE_SHOOT1;

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(-56.5, -46.5, Math.toRadians(225)));

        paths = new Paths(follower);
        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);

        pathTimer = new Timer();
        opModeTimer = new Timer();
        opModeTimer.resetTimer();

        isTurretTargeting.set(false);
        shooterTarget.set(0.0);
        oldTarget.set(0.0);
        isTurretTargeting.set(false);
        isTurret23Mode.set(false);

        turnError.set(0.0);
    }

    public enum PathState {
        DRIVE_SHOOT1,
        SHOOT1_WAIT, // New state specifically for waiting
        DONE,
        DRIVE_INTAKE1,
        DRIVE_SHOOT2,
        DRIVE_INTAKE1_WAIT,
        DRIVE_INTAKE2_X,
        DRIVE_INTAKE2_Y,
        DRIVE_SHOOT3,
        DRIVE_INTAKE3_X,
        DRIVE_INTAKE3_Y,
        DRIVE_SHOOT4,
    }

    public void setPathState(PathState runState){
        pathState = runState;
        pathTimer.resetTimer(); // Crucial: This resets the timer for the NEW state
    }

    @Override
    public void loop() {
        isTurretTargeting.set(false);
        follower.update();
        autonomousPathUpdate();

        // --- Turret Logic ---
        if(isTurretTargeting.get()){
            geometry.Pose pose = turret.getPoseWithLimey();
            double distance = pose.getY();
            double angle = pose.getAngle();
            double targetAngle = Math.toDegrees(Math.atan(Turret.LIMEY_LEFT_DISTANCE/distance));
            double error = targetAngle - angle;

            turnError.set(error);

            if(distance > 80){
                targetAngle = Math.toDegrees(Math.atan(Turret.LIMEY_LEFT_DISTANCE/distance));
                error = targetAngle - angle;
                double power = (-Math.signum(error)*Turret.TURRET_TARGETING_REST_POWER - error*Turret.TURRET_TARGETING_K)*0.5;

                if(Math.abs(error) > 1) {
                    turret.turn(power);
                } else {
                    turret.turn(0.0);
                }

                double targetRPM = turret.getShooterRPMFromLimelight();
                shooterTarget.set(targetRPM);
                if(!isTurret23Mode.get()) {
                    shooterTarget.set(targetRPM * Turret.SHOOT_RATIO_1);
                } else {
                    shooterTarget.set(targetRPM * Turret.SHOOT_RATIO_23);
                }
            } else {
                turret.turn(0.0);
                shooterTarget.set(2000.0);
            }
        } else {
            turret.turn(0.0);
            shooterTarget.set(0.0);
        }
        double st = shooterTarget.get();
        if(st != oldTarget.get()){
            if(st != 0) {

                turret.shooter.setTargetVelocity(st);
            }else{
                turret.shooter.resetRunMode();
                turret.shoot(0.0);
            }
            oldTarget.set(st);
        }
        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("Turret Target", isTurretTargeting.get());
        panelsTelemetry.update(telemetry);
        _loop();

    }

    @Override
    public void stop() {
        _stop();
    }

    public static class Paths {
        public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7, Path8,Path9;
        public Paths(Follower follower) {
            Path1 = follower.pathBuilder().addPath(
                            new BezierLine(new Pose(-56.5, -46.5), new Pose(-14, -14))
//            new BezierLine(new Pose(-56.5, -46.5), new Pose(0, 0))
                    ).setLinearHeadingInterpolation(Math.toRadians(225), Math.toRadians(225))
                    .setGlobalDeceleration(3)
                    .build();
            Path2 = follower.pathBuilder().addPath(
                            new BezierLine(new Pose(-14, -14), new Pose(-14, -51))
//            new BezierLine(new Pose(-56.5, -46.5), new Pose(0, 0))
                    ).setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(270))
                    .setGlobalDeceleration(3)
                    .build();
            Path3 = follower.pathBuilder().addPath(
                            new BezierLine(new Pose(-14, -51),new Pose(-14, -14))
//            new BezierLine(new Pose(-56.5, -46.5), new Pose(0, 0))
                    ).setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(225))
                    .setGlobalDeceleration(3)
                    .setVelocityConstraint(20)
                    .build();
            Path4 = follower.pathBuilder().addPath(
                            new BezierLine(new Pose(-14, -14),new Pose(8, -14))
//            new BezierLine(new Pose(-56.5, -46.5), new Pose(0, 0))
                    ).setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(270))
                    .setGlobalDeceleration(3)
                    .setVelocityConstraint(20)
                    .build();
            Path5 = follower.pathBuilder().addPath(
                            new BezierLine(new Pose(8, -14),new Pose(8, -55))
//            new BezierLine(new Pose(-56.5, -46.5), new Pose(0, 0))
                    ).setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(270))
                    .setGlobalDeceleration(3)
                    .setVelocityConstraint(20)
                    .build();
            Path6 = follower.pathBuilder().addPath(
                            new BezierLine(new Pose(8, -55),new Pose(-14, -14))
//            new BezierLine(new Pose(-56.5, -46.5), new Pose(0, 0))
                    ).setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(225))
                    .setGlobalDeceleration(3)
                    .setVelocityConstraint(20)
                    .build();
            Path7 = follower.pathBuilder().addPath(
                            new BezierLine(new Pose(-14, -14),new Pose(27, -14))
//            new BezierLine(new Pose(-56.5, -46.5), new Pose(0, 0))
                    ).setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(270))
                    .setGlobalDeceleration(3)
                    .setVelocityConstraint(20)
                    .build();
            Path8 = follower.pathBuilder().addPath(
                            new BezierLine(new Pose(27, -14),new Pose(27, -55))
//            new BezierLine(new Pose(-56.5, -46.5), new Pose(0, 0))
                    ).setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(270))
                    .setGlobalDeceleration(3)
                    .setVelocityConstraint(20)
                    .build();
            Path9 = follower.pathBuilder().addPath(
                            new BezierLine(new Pose(27, -55),new Pose(-14, -14))
//            new BezierLine(new Pose(-56.5, -46.5), new Pose(0, 0))
                    ).setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(225))
                    .setGlobalDeceleration(3)
                    .setVelocityConstraint(20)
                    .build();
        }
    }

    public PathState autonomousPathUpdate() {
        switch (pathState) {

            case DRIVE_SHOOT1:
                follower.followPath(paths.Path1, true);
//                isTurretTargeting.set(true); // Enable turret while driving
                setPathState(SHOOT1_WAIT);
                break;

//            case SHOOT1:
//                // Wait for path to finish AND turret to align
//                if(!follower.isBusy()) {
//                    // Check if aligned (error < 2 degrees) OR timeout (2 secs)
//
//                        ShootAuto.run(); // 1. Run the command ONCE
//
//                        // 2. Immediately switch to WAIT state.
//                        // This prevents ShootAuto from being called again in the next loop.
//                        setPathState(SHOOT1_WAIT);
//
//                }
//                break;
//
            case SHOOT1_WAIT:
                // 3. Stay in this state for 1 second to let the ShootAuto thread finish.
                // Since setPathState resets the timer, getElapsedTimeSeconds starts at 0 here.
                if (pathTimer.getElapsedTimeSeconds() > 2.0) {
                    setPathState(PathState.DRIVE_INTAKE1);
                }
                break;

            case DRIVE_INTAKE1:
//                intake.intakeAndFeed(1);
                if(!follower.isBusy()) {
                    follower.followPath(paths.Path2, true);
                    Intake.run();
                    setPathState(DRIVE_INTAKE1_WAIT);
                }
                break;
            case DRIVE_INTAKE1_WAIT:
                // 3. Stay in this state for 1 second to let the ShootAuto thread finish.
                // Since setPathState resets the timer, getElapsedTimeSeconds starts at 0 here.
                if (pathTimer.getElapsedTimeSeconds() > 2.0) {
                    setPathState(PathState.DRIVE_SHOOT2);
                }
                break;
            case DRIVE_SHOOT2:
                if(!follower.isBusy()){
                    follower.followPath(paths.Path3, true);
                    setPathState(DRIVE_INTAKE2_X);
                }
                break;
            case DRIVE_INTAKE2_X:
                if(!follower.isBusy()){
                    follower.followPath(paths.Path4, true);
                    Intake.run();
                    setPathState(PathState.DRIVE_INTAKE2_Y);
                }
                break;
            case DRIVE_INTAKE2_Y:
                if(!follower.isBusy()){
                    follower.followPath(paths.Path5, true);
                    setPathState(PathState.DRIVE_SHOOT3);
                }
                break;
            case DRIVE_SHOOT3:
                if(!follower.isBusy()){
                    follower.followPath(paths.Path6, true);
                    setPathState(DRIVE_INTAKE3_X);
                }
                break;
            case DRIVE_INTAKE3_X:
                if(!follower.isBusy()){
                    follower.followPath(paths.Path7, true);
                    setPathState(PathState.DRIVE_INTAKE3_Y);
                }
                break;
            case DRIVE_INTAKE3_Y:
                if(!follower.isBusy()){
                    follower.followPath(paths.Path8, true);
                    setPathState(PathState.DRIVE_SHOOT4);
                }
                break;
            case DRIVE_SHOOT4:
                if(!follower.isBusy()){
                    follower.followPath(paths.Path9, true);
                    setPathState(PathState.DONE);
                }
                break;
            case DONE:
                // Now it's safe to be done
                break;
        }

        return pathState;
    }
}