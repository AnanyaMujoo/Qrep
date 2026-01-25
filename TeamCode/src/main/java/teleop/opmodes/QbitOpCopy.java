package teleop.opmodes;

import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.intake;
import static robotparts.RobotConfig.turret;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import geometry.Pose;
import robotparts.hardware.Turret;
import teleop.Tele;
import teleop.TeleChain;
import teleop.teleutil.Button;

@TeleOp(name = "QbitOpCopy")
public class QbitOpCopy extends Tele {

    public static AtomicReference<Double> shooterTarget = new AtomicReference<>(0.0);
    public static AtomicReference<Double> oldTarget = new AtomicReference<>(0.0);
    public static AtomicReference<Double> turnError = new AtomicReference<>(0.0);

    public static AtomicBoolean isTurretTargeting = new AtomicBoolean(false);
    public static AtomicBoolean readyToShoot = new AtomicBoolean(false);

    private double ratio12 = 1.05;
    private double ratio23 = 1.10;
    private double time12 = 0.320;
    private double time23 = 0.320;

    private boolean sequenceRunning = false;
    private double lastVelocity = 0;
    private ElapsedTime seqTimer = new ElapsedTime();

    @Override
    public void initTele() {
        turret.shooter.setPIDF(25, 0, 0, 13.5);

        // --- GPA: MASTER CONTROLS ---
        gpA.onClick(Button.Y, TeleChain.RevampedIntake);
        gpA.onClick(Button.X, TeleChain.RevampedShoot);
        gpA.onClick(Button.B, () -> {
            isTurretTargeting.set(false);
            intake.intakeAndFeed(0);
        });

        // --- GPB: TUNING ---
        gpB.onClick(Button.LEFT_BUMPER, () -> ratio12 += 0.01);
        gpB.onClick(Button.X, () -> ratio12 -= 0.01);
        gpB.onClick(Button.Y, () -> ratio23 += 0.01);
        gpB.onClick(Button.RIGHT_BUMPER, () -> ratio23 -= 0.01);

        intake.lock();
        turret.shooter.softResetEncoder();
        isTurretTargeting.set(false);
        readyToShoot.set(false);
        shooterTarget.set(0.0);
    }

    @Override
    public void loopTele() {
        drive.move(0.7*gpA.ry, 0.7*gpA.rx, 0.6*gpA.lx);
        drive.updateOdometry();

        // --- ALL TELEMETRY RELAYED ---
        display("Odo X | Y | H", String.format("%.1f | %.1f | %.1f", drive.getX(), drive.getY(), drive.getHeading()));
        display("Color Sensor Dist", intake.getDetectDistance());
        display("Ball Detected?", intake.getDetectDistance() < 4.1);
        display("Shooter Target | Actual", shooterTarget.get() + " | " + turret.shooter.getVelocity());
        display("Turn Error", turnError.get());
        display("READY TO BLITZ", readyToShoot.get() ? "!!! YES !!!" : "AIMING...");

        if(isTurretTargeting.get()){
            Pose pose = turret.getPoseWithLimey();
            double distance = pose.getY();
            double angle = pose.getAngle();

            if(distance > 80){
                double targetAngle = Math.toDegrees(Math.atan(Turret.LIMEY_LEFT_DISTANCE/distance));
                double error = targetAngle - angle;
                turnError.set(error);

                double power = (-Math.signum(error)*Turret.TURRET_TARGETING_REST_POWER - error*Turret.TURRET_TARGETING_K)*0.5;
                turret.turn(Math.abs(error) > 1 ? power : 0.0);

//                double targetRPM = turret.getShooterRPMFromLimelight();
//                double actual = turret.shooter.getVelocity();

//                readyToShoot.set(Math.abs(error) < 1.5 && Math.abs(actual - targetRPM) < 150);

                // --- SUCCESSION LOGIC ---
//                double currentDip = lastVelocity - actual;
//                if (!sequenceRunning && currentDip > 140) {
//                    sequenceRunning = true;
//                    seqTimer.reset();
//                }
//                lastVelocity = actual;
//
//                if (sequenceRunning) {
//                    double elapsed = seqTimer.seconds();
//                    if (elapsed < time12) shooterTarget.set(targetRPM * ratio12);
//                    else if (elapsed < (time12 + time23)) shooterTarget.set(targetRPM * ratio23);
//                    else { sequenceRunning = false; shooterTarget.set(targetRPM); }
//                } else {
//                    shooterTarget.set(targetRPM * Turret.SHOOT_RATIO_1);
//                }
            } else {
                turret.turn(0.0);
                shooterTarget.set(2000.0);
                readyToShoot.set(false);
            }
        } else {
            turret.turn(0.0);
            shooterTarget.set(0.0);
            sequenceRunning = false;
        }

        double st = shooterTarget.get();
        if(st != oldTarget.get()){
            if(st != 0) {
                turret.shooter.setTargetVelocity(st);
            } else {
                turret.shooter.resetRunMode();
                turret.shoot(0.0);
            }
            oldTarget.set(st);
        }
    }
}