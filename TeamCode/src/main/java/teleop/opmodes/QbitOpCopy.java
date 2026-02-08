package teleop.opmodes;

import static global.Common.gamepad2;
import static global.General.fieldSide;
import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.intake;
import static robotparts.RobotConfig.turret;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.geometry.BezierLine;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import elements.FieldSide;
import geometry.Pose;
import global.Common;
import pedroPathing.Constants;
import robotparts.hardware.Turret;
import teleop.Tele;
import teleop.TeleChainCopy; // <--- IMPORTANT IMPORT
import teleop.teleutil.Button;
import utility.Timer;

@TeleOp
public class QbitOpCopy extends Tele {

    // --- INDEPENDENT VARIABLES ---
    public static AtomicReference<Double> shooterTarget = new AtomicReference<>(0.0);
    public static AtomicReference<Double> oldTarget = new AtomicReference<>(0.0);
    public static AtomicReference<Double> turnError = new AtomicReference<>(0.0);

    public static AtomicBoolean isTurretTargeting = new AtomicBoolean(false);
    public static AtomicBoolean isTurretManualFar = new AtomicBoolean(false);

    public static AtomicBoolean isAutoMode = new AtomicBoolean(false);
    public static AtomicBoolean isTurretPresetMode = new AtomicBoolean(false);
    public static AtomicBoolean isTurret23Mode = new AtomicBoolean(false);
    public static AtomicBoolean readyToShoot = new AtomicBoolean(false);
    public static AtomicBoolean farMode = new AtomicBoolean(true);
    public static AtomicBoolean isTurret23ManualMode = new AtomicBoolean(false);
    public static AtomicBoolean isTurretManualClose = new AtomicBoolean(false);

    public static boolean ScalerMode = false;

    // POSITIONS
    public static Pose2D RESET_POSE = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
    public static Pose2D SHOOT_POSE = new Pose2D(DistanceUnit.INCH, -60, 20, AngleUnit.DEGREES, 0);
    public static Pose2D INTAKE_POSE = new Pose2D(DistanceUnit.INCH, -20, 12, AngleUnit.DEGREES, 0);


    public static double TURRET_SHOOT_ANGLE = -110.0;
    public int i = 0;
    public Timer timer = new Timer();
    public Follower follower;

    @Override
    public void initTele() {
        isTurretTargeting.set(false);
        isTurretManualFar.set(false);
        isTurretManualClose.set(false);
        if (fieldSide == FieldSide.RED){
            RESET_POSE = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
            SHOOT_POSE = new Pose2D(DistanceUnit.INCH, -60, -18, AngleUnit.DEGREES, 0);
            INTAKE_POSE = new Pose2D(DistanceUnit.INCH, -15, -12, AngleUnit.DEGREES, 0);
            TURRET_SHOOT_ANGLE = 110.0;


        }
        if (fieldSide == FieldSide.BLUE){
            RESET_POSE = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
            SHOOT_POSE = new Pose2D(DistanceUnit.INCH, -60, 20, AngleUnit.DEGREES, 0);
            INTAKE_POSE = new Pose2D(DistanceUnit.INCH, -20, 12, AngleUnit.DEGREES, 0);
            TURRET_SHOOT_ANGLE = -110.0;


        }
        i=0;


        //GAMEPAD B

        //manual intake
        gpB.onClick(Button.Y, () ->{
            TeleChainCopy.JustIntake.run();
        });
        //manual shoot
        gpB.onClick(Button.X, TeleChainCopy.JustShootFar);

        gpB.onClick(Button.A, TeleChainCopy.JustShootClose);

        gpA.onClick(Button.DPAD_UP, () -> Turret.ANGLE_OFFSET += 0.5);
        gpA.onClick(Button.DPAD_DOWN, () -> Turret.ANGLE_OFFSET -= 0.5);

        gpB.onPress(Button.LEFT_TRIGGER, () -> {
            switchToManual();
            // Force motors to zero immediately just in case
            drive.move(0, 0, 0);
        });


        turret.shooter.setPIDF(25, 0, 0.00000, 13.5);
//         PIDF(6, 0, 0, 14);
        // Use TeleChainCopy!
        gpA.onClick(Button.Y, TeleChainCopy.IntakeAuto);

        // --- BUTTON X: TARGET AND FIRE ---
        gpA.onClick(Button.X, () -> {
            isTurretPresetMode.set(false);
            isTurretTargeting.set(true);

            if(farMode.get()){
                TeleChainCopy.ShootFar.run();
            }else{
                TeleChainCopy.Shoot.run();
            }
        });

        turret.turret.hardResetEncoder();
        drive.pinpoint.resetPosAndIMU();

        FollowerConstants followerConstants2 = new FollowerConstants()
                .mass(12.55)
                .forwardZeroPowerAcceleration(-43.8014)
                .lateralZeroPowerAcceleration(-74.98348806)
                .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.0079, 0.0, 0.00, 0.00, 0.04))
                .translationalPIDFCoefficients(new PIDFCoefficients(0.0079, 0.0, 0.00, 0.03));
        follower = new FollowerBuilder(followerConstants2, hardwareMap)
                .pinpointLocalizer(Constants.localizerConstants)
                .pathConstraints(Constants.pathConstraints)
                .mecanumDrivetrain(Constants.driveConstants)
                .build();
        follower.setStartingPose(new com.pedropathing.geometry.Pose(0, 0, Math.toRadians(0)));

        gpA.onClick(Button.RIGHT_BUMPER, this::switchToAuto);
        gpA.onClick(Button.LEFT_BUMPER, this::switchToManual);

        gpA.onClick(Button.A, () -> {
            drive.pinpoint.setPosition(RESET_POSE);
            follower.setPose(new com.pedropathing.geometry.Pose(
                    RESET_POSE.getX(DistanceUnit.INCH),
                    RESET_POSE.getY(DistanceUnit.INCH),
                    RESET_POSE.getHeading(AngleUnit.RADIANS)));
        });
        gpA.onClick(Button.RIGHT_BUMPER, () -> {
            i+=1;
            if(i==1) {
                isTurretManualFar.set(false);
                isTurretManualClose.set(false);
                isTurretTargeting.set(false);
                switchToAuto();
                isTurretPresetMode.set(true);
                turret.turret.setTarget(TURRET_SHOOT_ANGLE, 1.0);

                Pose2D currentPosition = drive.pinpoint.getPosition();
                double finalAngle = SHOOT_POSE.getHeading(AngleUnit.RADIANS);

                follower.followPath(follower.pathBuilder()
                        .addPath(new BezierLine(
                                new com.pedropathing.geometry.Pose(currentPosition.getX(DistanceUnit.INCH), currentPosition.getY(DistanceUnit.INCH), currentPosition.getHeading(AngleUnit.RADIANS)),
                                new com.pedropathing.geometry.Pose(SHOOT_POSE.getX(DistanceUnit.INCH), SHOOT_POSE.getY(DistanceUnit.INCH), finalAngle)
                        ))
                        .setLinearHeadingInterpolation(currentPosition.getHeading(AngleUnit.RADIANS), finalAngle)
                        .build());
            }
            else{
                switchToAuto();
                isTurretPresetMode.set(true);


                Pose2D currentPosition = drive.pinpoint.getPosition();
                double finalAngle = SHOOT_POSE.getHeading(AngleUnit.RADIANS);

                follower.followPath(follower.pathBuilder()
                        .addPath(new BezierLine(
                                new com.pedropathing.geometry.Pose(currentPosition.getX(DistanceUnit.INCH), currentPosition.getY(DistanceUnit.INCH), currentPosition.getHeading(AngleUnit.RADIANS)),
                                new com.pedropathing.geometry.Pose(SHOOT_POSE.getX(DistanceUnit.INCH), SHOOT_POSE.getY(DistanceUnit.INCH), finalAngle)
                        ))
                        .setLinearHeadingInterpolation(currentPosition.getHeading(AngleUnit.RADIANS), finalAngle)
                        .build());
            }
        }
        );
        gpA.onClick(Button.LEFT_BUMPER, () -> {
            switchToAuto();
//            isTurretPresetMode.set(true);
            isTurretTargeting.set(false);
            isTurretManualFar.set(false);
            isTurretManualClose.set(false);
//            turret.turret.setTarget(TURRET_SHOOT_ANGLE, 1.0);

            Pose2D currentPosition = drive.pinpoint.getPosition();
            double finalAngle = SHOOT_POSE.getHeading(AngleUnit.RADIANS);

            follower.followPath(follower.pathBuilder()
                    .addPath(new BezierLine(
                            new com.pedropathing.geometry.Pose(currentPosition.getX(DistanceUnit.INCH), currentPosition.getY(DistanceUnit.INCH), finalAngle),
                            new com.pedropathing.geometry.Pose(INTAKE_POSE.getX(DistanceUnit.INCH), INTAKE_POSE.getY(DistanceUnit.INCH),finalAngle)
                    ))
                    .setLinearHeadingInterpolation(currentPosition.getHeading(AngleUnit.RADIANS),finalAngle )
                    .build());
        });

        gpB.onClick(Button.DPAD_UP, () -> Turret.SHOOT_OFFSET_1 += 20.0);
        gpB.onClick(Button.DPAD_DOWN, () -> Turret.SHOOT_OFFSET_1 -= 20.0);
        gpB.onClick(Button.DPAD_RIGHT, () -> Turret.SHOOT_OFFSET_23 += 20.0);
        gpB.onClick(Button.DPAD_LEFT, () -> Turret.SHOOT_OFFSET_23 -= 20.0);
        gpA.onClick(Button.RIGHT_TRIGGER, () -> ScalerMode = !ScalerMode);


        readyToShoot.set(false);
        farMode.set(true);
        shooterTarget.set(0.0);
        oldTarget.set(0.0);
        isTurretTargeting.set(false);
        isTurret23Mode.set(false);
        turnError.set(0.0);

        turret.turret.softResetEncoder();
        intake.lock();
        if (intake.feeder != null) intake.feeder.softResetEncoder();
        turret.shooter.softResetEncoder();
        timer.reset();
    }

    @Override
    public void startTele() {}

    public void switchToManual() {
        follower.breakFollowing();
        follower.setDrivePIDFCoefficients(new FilteredPIDFCoefficients(0, 0, 0, 0, 0));
        follower.setTranslationalPIDFCoefficients(new PIDFCoefficients(0, 0, 0, 0));
        follower.setHeadingPIDFCoefficients(new PIDFCoefficients(0, 0, 0, 0));
        follower.setSecondaryDrivePIDFCoefficients(new FilteredPIDFCoefficients(0, 0, 0, 0, 0));
        follower.setSecondaryTranslationalPIDFCoefficients(new PIDFCoefficients(0, 0, 0, 0));
        drive.move(0, 0, 0);
        isAutoMode.set(false);
    }

    public void switchToAuto() {
        follower.breakFollowing();
        follower.setDrivePIDFCoefficients(new FilteredPIDFCoefficients(0.0079, 0.0, 0.00, 0.00, 0.04));
        follower.setTranslationalPIDFCoefficients(new PIDFCoefficients(0.0079, 0.0, 0.00, 0.03));
        drive.move(0, 0, 0);
        isAutoMode.set(true);
    }

    @Override
    public void loopTele() {
        follower.update();

        if (isAutoMode.get() && !follower.isBusy()) {
            switchToManual();
            isTurretPresetMode.set(false);
            isTurretTargeting.set(true);
        }

        if (!isAutoMode.get()) {
            drive.move(0.8 * gpA.ry, 0.8 * gpA.rx, 0.6 * gpA.lx);
        }
        drive.updateOdometry();

        // --- DEBUG TELEMETRY ---
        double currentRPM = turret.shooter.getVelocity();
        double targetRPM = shooterTarget.get();
        double rpmError = Math.abs(currentRPM - targetRPM);
        double angleErr = Math.abs(turnError.get());

//        display("--- DEBUG (COPY) ---", "");
//        display("RPM Error", String.format("%.1f (Need < 150)", rpmError));
//        display("Angle Error", String.format("%.2f (Need < 1.5)", angleErr));
//        display("READY TO BLITZ", readyToShoot.get() ? "!!! YES !!!" : "AIMING...");
//        display("isTurretTargeting", isTurretTargeting.get());
        display("Offset1", Turret.SHOOT_OFFSET_1);
        display("Offset23", Turret.SHOOT_OFFSET_23);
        display("ANGLE OFFSET", Turret.ANGLE_OFFSET);
//        display("Distance", turret.getPoseY());
//        display("DistanceX", drive.getX());
//        display("TargetRPM", shooterTarget.get());


        // --- TURRET LOGIC ---
        if ((isTurretTargeting.get() && drive.getX()<-40 ) && !isTurretManualFar.get() && !isTurretManualClose.get()){
            Pose pose = turret.getPoseWithLimey();
            double distance = pose.y;
            double distanceInches = turret.getDistanceInches();
            double angle = pose.getAngle();
            double targetAngle = Math.toDegrees(Math.atan(Turret.LIMEY_LEFT_DISTANCE / distance));
            double error = targetAngle - angle;

            if (distance > 30) {
                targetAngle = Math.toDegrees(Math.atan(Turret.LIMEY_LEFT_DISTANCE / distance));
                error = targetAngle - (angle);
                turnError.set(error);

                if (timer.seconds() > 0.5 && Math.abs(error) > 0.5) {
                    turret.turret.softResetEncoder();
                    turret.turret.setTarget(-error, 0.1);
                    timer.reset();
                }

                if (!isTurret23Mode.get()) {
                    if (ScalerMode) {
                        shooterTarget.set(turret.getRPM1(distanceInches) + Turret.SHOOT_OFFSET_1);
                    } else {
                        shooterTarget.set(turret.getClosestRPM1(distanceInches) + Turret.SHOOT_OFFSET_1);
                    }
                } else {
                    if (ScalerMode) {
                        shooterTarget.set(turret.getRPM23(distanceInches) + Turret.SHOOT_OFFSET_23);
                    } else {
                        shooterTarget.set(turret.getClosestRPM23(distanceInches) + Turret.SHOOT_OFFSET_23);
                    }
                }
                farMode.set(distance > 83);

                // --- READY CHECK ---
                boolean isAngleGood = Math.abs(error) < 1.5;
                boolean isRpmGood = rpmError < 150;
                readyToShoot.set(isAngleGood && isRpmGood);

            } else {
                turret.turn(0.0);
                shooterTarget.set(2000.0);
                readyToShoot.set(false);
            }
        }

       else if (isTurretManualFar.get()){

                if (!isTurret23Mode.get()) {
                        shooterTarget.set(turret.getClosestRPM1(107) + Turret.SHOOT_OFFSET_1);
                    }
                 else {
                        shooterTarget.set(turret.getClosestRPM23(107) + Turret.SHOOT_OFFSET_23);
                    }

                farMode.set(true);

                // --- READY CHECK ---
                boolean isAngleGood = true;
                boolean isRpmGood = rpmError < 150;
                readyToShoot.set(isAngleGood && isRpmGood);

        }

        else if (isTurretManualClose.get()){

            if (!isTurret23Mode.get()) {
                shooterTarget.set(turret.getClosestRPM1(47) + Turret.SHOOT_OFFSET_1);
            }
            else {
                shooterTarget.set(turret.getClosestRPM23(47) + Turret.SHOOT_OFFSET_23);
            }

            farMode.set(false);

            // --- READY CHECK ---
            boolean isAngleGood = true;
            boolean isRpmGood = rpmError < 150;
            readyToShoot.set(isAngleGood && isRpmGood);

        }
        else {
            if (!isTurretPresetMode.get()) {
                turret.turn(0.0);
                shooterTarget.set(0.0);
                turret.turret.resetRunMode();
                readyToShoot.set(false);
            }
        }
        double st = shooterTarget.get();
        if (st != oldTarget.get()) {
            if (st != 0) {
                turret.shooter.setTargetVelocity(st);
            } else {
                turret.shooter.resetRunMode();
                turret.shoot(0.0);
            }
            oldTarget.set(st);
        }
    }

    @Override
    public void stopTele() {
        turret.limey.stop();
    }

    @TeleOp(name = "BlueTeleOpCopy", group = "TeleOp")
    public static class BlueTeleOp extends QbitOpCopy {{
        fieldSide = FieldSide.BLUE;
    }}

    @TeleOp(name = "RedTeleOpCopy", group = "TeleOp")
    public static class RedTeleOp extends QbitOpCopy {{
        fieldSide = FieldSide.RED;
    }}
}