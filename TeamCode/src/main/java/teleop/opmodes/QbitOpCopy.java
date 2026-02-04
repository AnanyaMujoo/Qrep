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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import elements.FieldSide;
import geometry.Pose;
import global.Common;
import pedroPathing.Constants;
import robotparts.hardware.Turret;
import teleop.Tele;
import teleop.teleutil.Button;
import utility.Timer;

@TeleOp
public class QbitOpCopy extends Tele {

    public static AtomicReference<Double> shooterTarget = new AtomicReference<>(0.0);
    public static AtomicReference<Double> oldTarget = new AtomicReference<>(0.0);
    public static AtomicReference<Double> turnError = new AtomicReference<>(0.0);

    public static AtomicBoolean isTurretTargeting = new AtomicBoolean(false);
    public static AtomicBoolean isAutoMode = new AtomicBoolean(false);

    // FLAGS
    public static AtomicBoolean isTurretPresetMode = new AtomicBoolean(false);
    public static AtomicBoolean requestFire = new AtomicBoolean(false);

    // !!! IMPORTANT: This flag ensures we stay in firing mode !!!
    public static AtomicBoolean isShootingBurst = new AtomicBoolean(false);

    public static AtomicBoolean isTurret23Mode = new AtomicBoolean(false);
    public static AtomicBoolean readyToShoot = new AtomicBoolean(false);
    public static AtomicBoolean farMode = new AtomicBoolean(false);
    public static boolean ScalerMode = false;

    public ArrayList<Double> angleArray = new ArrayList<>();

    // POSITIONS
    public static final Pose2D RESET_POSE = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
    public static final Pose2D SHOOT_POSE = new Pose2D(DistanceUnit.INCH, -66, 21, AngleUnit.DEGREES, 0);
    public static final double TURRET_SHOOT_ANGLE = -110.0;

    public Timer timer = new Timer();
    public Timer shootTimer = new Timer();
    public Follower follower;
    public int autoIndex = 0;

    @Override
    public void initTele() {
        turret.shooter.setPIDF(25, 0, 0.00000, 13.5);
        gpA.onClick(Button.Y, Intake);

        // --- BUTTON X: TARGET AND FIRE ---
        gpA.onClick(Button.X, () -> {
            isTurretPresetMode.set(false);
            isTurretTargeting.set(true);
            requestFire.set(true);
            isShootingBurst.set(false);  // Reset state

            if(farMode.get()){
                ShootFar.run();
            }else{
                Shoot.run();
            }
        });

        turret.turret.hardResetEncoder();
        drive.pinpoint.resetPosAndIMU();

        FilteredPIDFCoefficients zeroFiltered = new FilteredPIDFCoefficients(0, 0, 0, 0, 0);
        PIDFCoefficients zeroPID = new PIDFCoefficients(0, 0, 0, 0);

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

        // --- BUTTON B: PATH + AIM (NO SHOOT) ---
        gpA.onClick(Button.B, () -> {
            switchToAuto();
            isTurretPresetMode.set(true);
            turret.turret.setTarget(TURRET_SHOOT_ANGLE, 1.0);
            requestFire.set(false);
            isShootingBurst.set(false);

            Pose2D currentPosition = drive.pinpoint.getPosition();
            double finalAngle = SHOOT_POSE.getHeading(AngleUnit.RADIANS);

            follower.followPath(follower.pathBuilder()
                    .addPath(new BezierLine(
                            new com.pedropathing.geometry.Pose(currentPosition.getX(DistanceUnit.INCH), currentPosition.getY(DistanceUnit.INCH), currentPosition.getHeading(AngleUnit.RADIANS)),
                            new com.pedropathing.geometry.Pose(SHOOT_POSE.getX(DistanceUnit.INCH), SHOOT_POSE.getY(DistanceUnit.INCH), finalAngle)
                    ))
                    .setLinearHeadingInterpolation(currentPosition.getHeading(AngleUnit.RADIANS), finalAngle)
                    .build());
        });

        gpA.onClick(Button.DPAD_UP, () -> Turret.SHOOT_OFFSET_1 += 20.0);
        gpA.onClick(Button.DPAD_DOWN, () -> Turret.SHOOT_OFFSET_1 -= 20.0);
        gpA.onClick(Button.DPAD_RIGHT, () -> Turret.SHOOT_OFFSET_23 += 20.0);
        gpA.onClick(Button.DPAD_LEFT, () -> Turret.SHOOT_OFFSET_23 -= 20.0);
        gpA.onClick(Button.RIGHT_TRIGGER, () -> ScalerMode = !ScalerMode);
        gpB.onClick(Button.B, JustIntake);

        readyToShoot.set(false);
        turret.turret.softResetEncoder();
        farMode.set(false);
        intake.lock();
        if (intake.feeder != null) intake.feeder.softResetEncoder();
        turret.shooter.softResetEncoder();
        shooterTarget.set(0.0);
        oldTarget.set(0.0);
        isTurretTargeting.set(false);
        isTurret23Mode.set(false);
        turnError.set(0.0);
        angleArray = new ArrayList<>();
        angleArray.add(0.0);
        angleArray.add(0.0);
        timer.reset();
        shootTimer.reset();
    }

    @Override
    public void startTele() {}

    public void switchToManual() {
        follower.breakFollowing();
        follower.setDrivePIDFCoefficients(new FilteredPIDFCoefficients(0, 0, 0, 0, 0));
        follower.setTranslationalPIDFCoefficients(new PIDFCoefficients(0, 0, 0, 0));
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

        // 1. ARRIVAL LOGIC
        if (isAutoMode.get() && !follower.isBusy()) {
            switchToManual();
            isTurretPresetMode.set(false);
            isTurretTargeting.set(true); // Switch to aiming
            requestFire.set(false);      // Ensure we don't shoot automatically from B
        }

        if (!isAutoMode.get()) {
            drive.move(0.7 * gpA.ry, 0.7 * gpA.rx, 0.6 * gpA.lx);
        }
        drive.updateOdometry();

        display("Target", Math.round(shooterTarget.get()));
        display("Distance", turret.getPoseY());
        display("Offset1", Turret.SHOOT_OFFSET_1);
        display("READY TO BLITZ", readyToShoot.get() ? "!!! YES !!!" : "AIMING...");

        // --- TURRET LOGIC ---
        if (isTurretTargeting.get()) {
            Pose pose = turret.getPoseWithLimey();
            double distance = pose.y;
            double distanceInches = turret.getDistanceInches();
            double angle = pose.getAngle();
            double targetAngle = Math.toDegrees(Math.atan(Turret.LIMEY_LEFT_DISTANCE / distance));
            double error = targetAngle - angle;

            if (distance > 80) {
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
                farMode.set(distance > 90);

                // --- SHOOTING BURST LOGIC ---
                if (requestFire.get()) {
                    // IF WE ARE ALREADY BURSTING, IGNORE CONDITIONS AND KEEP SHOOTING
                    if (isShootingBurst.get()) {
                        if (shootTimer.seconds() < 3.0) {
                            // KEEP MOTORS ON
                            intake.intake(1.0);
                            intake.feed(1.0);
                            readyToShoot.set(true);
                        } else {
                            // TIME IS UP -> STOP EVERYTHING
                            isShootingBurst.set(false);
                            requestFire.set(false);
                            isTurretTargeting.set(false);
                            shooterTarget.set(0.0);

                            intake.intake(0.0);
                            intake.feed(0.0);
                            readyToShoot.set(false);
                        }
                    } else {
                        // WE ARE NOT BURSTING YET -> CHECK CONDITIONS TO START
                        double currentRPM = turret.shooter.getVelocity();
                        boolean angleGood = Math.abs(error) < 2.5;
                        boolean rpmGood = Math.abs(currentRPM - shooterTarget.get()) < 150;

                        if (angleGood && rpmGood) {
                            // START THE BURST!
                            isShootingBurst.set(true);
                            shootTimer.reset();

                            // START MOTORS IMMEDIATELY
                            intake.intake(1.0);
                            intake.feed(1.0);
                            readyToShoot.set(true);
                        } else {
                            // WAITING FOR LOCK
                            intake.intake(0.0);
                            intake.feed(0.0);
                            readyToShoot.set(false);
                        }
                    }
                } else {
                    // Not requesting fire (Button B mode)
                    // Do not force 0.0 here so manual intake still works if needed
                }
                // --- SHOOTING BURST LOGIC END ---

            } else {
                turret.turn(0.0);
                shooterTarget.set(2000.0);
                readyToShoot.set(false);
                isShootingBurst.set(false);

                // If we were trying to fire but target was lost, stop intake
                if(requestFire.get()) {
                    intake.intake(0.0);
                    intake.feed(0.0);
                }
            }
        } else {
            // NOT TARGETING
            if (!isTurretPresetMode.get()) {
                turret.turn(0.0);
                shooterTarget.set(0.0);
                turret.turret.resetRunMode();
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