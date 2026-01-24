package teleop.testopmodes;

import static robotparts.RobotConfig.turret;
import static robotparts.RobotConfig.intake;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import robotparts.hardware.Turret;
import teleop.Tele;
import teleop.teleutil.Button;
import geometry.Pose;

@TeleOp(name = "KickstartBlitzTuner", group = "Tuning")
public class ShooterTuner extends Tele {

    private double p = 50.0; // Proportional Gain
    private double f = 13.7; // Feedforward

    private double ratio12 = 1.20; // Ball 1 -> 2
    private double ratio23 = 1.30; // Ball 2 -> 3

    private double recoveryBoost = 1.08;
    private double dipThreshold = 40.0;
    private double recoveryTolerance = 50.0;

    private boolean shooterActive = false;
    private boolean isRecovering = false;
    private int ballCount = 0;
    private double lastVelocity = 0;

    @Override
    public void initTele() {
        // Toggle Shooter
        gpA.onClick(Button.B, () -> {
            shooterActive = !shooterActive;
            isRecovering = false;
            ballCount = 0;
            if (!shooterActive) {
                turret.shooter.setTargetVelocity(0);
                turret.turn(0);
            }
        });

        // --- TUNING P (Vertical D-Pad) ---
        gpA.onClick(Button.DPAD_UP,   () -> p += 2.0);
        gpA.onClick(Button.DPAD_DOWN, () -> p -= 2.0);

        // --- TUNING RATIO 1-2 (Bumpers) ---
        gpA.onClick(Button.RIGHT_BUMPER, () -> ratio12 += 0.01);
        gpA.onClick(Button.LEFT_BUMPER,  () -> ratio12 -= 0.01);

        // --- TUNING RATIO 2-3 (Horizontal D-Pad) ---
        gpA.onClick(Button.DPAD_RIGHT,   () -> ratio23 += 0.01);
        gpA.onClick(Button.DPAD_LEFT,    () -> ratio23 -= 0.01);
    }

    @Override
    public void loopTele() {
        intake.intakeAndFeed(gpA.rt);

        // 1. TURRET TARGETING
        Pose pose = turret.getPoseWithLimey();
        double distance = pose.getY();
        double angle = pose.angle;

        if (shooterActive && distance > 10) {
            double targetAngle = Math.toDegrees(Math.atan(Turret.LIMEY_LEFT_DISTANCE / distance));
            double error = targetAngle - angle;
            if (Math.abs(error) > 0.8) {
                double power = (-Math.signum(error) * Turret.TURRET_TARGETING_REST_POWER - error * Turret.TURRET_TARGETING_K) * 0.5;
                turret.turn(power);
            } else {
                turret.turn(0.0);
            }
        } else {
            turret.turn(0);
        }

        // 2. VELOCITY & DIP DETECTION
        double actual = turret.shooter.getVelocity();
        double baseTarget = shooterActive ? turret.getShooterRPMFromLimelight() * Turret.SHOOT_RATIO_1 : 0;

        double currentDip = lastVelocity - actual;

        if (shooterActive && !isRecovering && currentDip > dipThreshold) {
            isRecovering = true;
            ballCount++;
        }

        // 3. MULTI-BALL TARGET LOGIC
        double finalTarget = baseTarget;

        if (shooterActive) {
            double stepTarget;
            if (ballCount == 0)      stepTarget = baseTarget;
            else if (ballCount == 1) stepTarget = baseTarget * ratio12;
            else                     stepTarget = baseTarget * ratio23;

            if (isRecovering) {
                finalTarget = stepTarget * recoveryBoost;
                if (actual >= (stepTarget - recoveryTolerance)) {
                    isRecovering = false;
                    if (ballCount >= 3) ballCount = 0;
                }
            } else {
                finalTarget = stepTarget;
            }
        }

        // Safety cap
        if (finalTarget > 3800) finalTarget = 3800;

        // Apply updated P-gain every loop
        turret.shooter.setPIDF(p, 0, 0, f);
        turret.shooter.setTargetVelocity(shooterActive ? finalTarget : 0);
        lastVelocity = actual;

        // --- TELEMETRY ---
        display("--- PID & BLITZ ---", "");
        display("P Gain", p);
        display("Ratio 1-2", String.format("%.2f", ratio12));
        display("Ratio 2-3", String.format("%.2f", ratio23));
        display("--- STATUS ---", "");
        display("Current Ball", (ballCount + 1));
        display("Recovering?", isRecovering);
        display("Actual/Target", (int)actual + " / " + (int)finalTarget);
    }
}