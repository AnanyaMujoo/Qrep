package pedroPathing;

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

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;
import static robotparts.RobotConfig.intake;
import static robotparts.RobotConfig.turret;
import static teleop.TeleChain.Intake;
import static teleop.TeleChain.ShootAuto;
import static teleop.opmodes.QbitOp.isTurret23Mode;
import static teleop.opmodes.QbitOp.isTurretTargeting;
import static teleop.opmodes.QbitOp.oldTarget;
import static teleop.opmodes.QbitOp.shooterTarget;
import static teleop.opmodes.QbitOp.turnError;

@Autonomous(name = "QAutoBlitz", group = "Autonomous")
@Configurable
public class QAutoBlitz extends OpMode implements Initializer {

    // --- Local Enum Definition (No dependency on QAuto) ---
    public enum PathState {
        DRIVE_SHOOT1,
        SHOOT1,
        SHOOT1_WAIT,
        DRIVE_INTAKE1,
        DRIVE_INTAKE1_WAIT,
        DRIVE_SHOOT2,
        DRIVE_INTAKE2_PUSH,
        DRIVE_SHOOT3,
        DRIVE_INTAKE3_PUSH,
        DRIVE_SHOOT4,
        DONE
    }

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
        isTurret23Mode.set(false);
        turnError.set(0.0);
    }

    public void setPathState(PathState runState){
        pathState = runState;
        pathTimer.resetTimer();
    }

    @Override
    public void loop() {
        isTurretTargeting.set(false);
        follower.update();
        autonomousPathUpdate();
        turret.shooter.setPIDF(25, 0, 0.00000, 13.5);

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
        panelsTelemetry.update(telemetry);
        _loop();
    }

    @Override
    public void stop() {
        _stop();
    }

    public static class Paths {
        public PathChain Path1, Path2, Path3;
        public PathChain Path4_5_Intake;
        public PathChain Path6;
        public PathChain Path7_8_Intake;
        public PathChain Path9;

        public Paths(Follower follower) {
            Path1 = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(-56.5, -46.5), new Pose(-14, -14)))
                    .setLinearHeadingInterpolation(Math.toRadians(225), Math.toRadians(225))
                    .setGlobalDeceleration(30)
                    .build();

            Path2 = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(-14, -14), new Pose(-14, -51)))
                    .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(270))
                    .setGlobalDeceleration(30)
                    .build();

            Path3 = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(-14, -51),new Pose(-14, -14)))
                    .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(225))
                    .setGlobalDeceleration(30)
                    .setVelocityConstraint(20)
                    .build();

            // Combined Intake Path 1
            Path4_5_Intake = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(-14, -14),new Pose(8, -14)))
                    .setLinearHeadingInterpolation(Math.toRadians(225), Math.toRadians(270))
                    .addPath(new BezierLine(new Pose(8, -14),new Pose(8, -55)))
                    .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(270))
                    .setGlobalDeceleration(3)
                    .setVelocityConstraint(20)
                    .build();

            Path6 = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(8, -55),new Pose(-14, -14)))
                    .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(225))
                    .setGlobalDeceleration(3)
                    .setVelocityConstraint(20)
                    .build();

            // Combined Intake Path 2
            Path7_8_Intake = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(-14, -14),new Pose(27, -14)))
                    .setLinearHeadingInterpolation(Math.toRadians(225), Math.toRadians(270))
                    .addPath(new BezierLine(new Pose(27, -14),new Pose(27, -55)))
                    .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(270))
                    .setGlobalDeceleration(3)
                    .setVelocityConstraint(20)
                    .build();

            Path9 = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(27, -55),new Pose(-14, -14)))
                    .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(225))
                    .setGlobalDeceleration(3)
                    .setVelocityConstraint(20)
                    .build();
        }
    }

    public PathState autonomousPathUpdate() {
        switch (pathState) {
            case DRIVE_SHOOT1:
                follower.followPath(paths.Path1, true);
                setPathState(PathState.SHOOT1);
                break;
            case SHOOT1:
                if(!follower.isBusy()) {
                    setPathState(PathState.SHOOT1_WAIT);
                }
                break;
            case SHOOT1_WAIT:
                if (pathTimer.getElapsedTimeSeconds() > 2.0) {
                    setPathState(PathState.DRIVE_INTAKE1);
                }
                break;
            case DRIVE_INTAKE1:
                if(!follower.isBusy()) {
                    follower.followPath(paths.Path2, true);
                    setPathState(PathState.DRIVE_INTAKE1_WAIT);
                }
                break;
            case DRIVE_INTAKE1_WAIT:
                if (pathTimer.getElapsedTimeSeconds() > 2.0) {
                    setPathState(PathState.DRIVE_SHOOT2);
                }
                break;

            case DRIVE_SHOOT2:
                if(!follower.isBusy()){
                    follower.followPath(paths.Path3, true);
                    setPathState(PathState.DRIVE_INTAKE2_PUSH);
                }
                break;

            case DRIVE_INTAKE2_PUSH:
                if(!follower.isBusy()){
                    follower.followPath(paths.Path4_5_Intake, true);
                    setPathState(PathState.DRIVE_SHOOT3);
                }
                break;

            case DRIVE_SHOOT3:
                if(!follower.isBusy()){
                    follower.followPath(paths.Path6, true);
                    setPathState(PathState.DRIVE_INTAKE3_PUSH);
                }
                break;

            case DRIVE_INTAKE3_PUSH:
                if(!follower.isBusy()){
                    follower.followPath(paths.Path7_8_Intake, true);
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
                break;
        }

        return pathState;
    }
}