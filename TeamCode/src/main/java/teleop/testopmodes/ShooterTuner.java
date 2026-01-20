package teleop.testopmodes;

import static robotparts.RobotConfig.turret;
import static robotparts.RobotConfig.intake;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import robotparts.hardware.Turret;
import teleop.Tele;
import teleop.teleutil.Button;
import geometry.Pose;

@TeleOp(name = "KickstartBlitzTuner", group = "Tuning")
public class ShooterTuner extends Tele {

    private double p = 75.0;
    private double f = 13.7;
    private double successionCoeff = 0.04;
    private double stepTime = 0.30; // Time between Ball 1 -> 2 and Ball 2 -> 3

    private boolean shooterActive = false;
    private boolean sequenceRunning = false;
    private ElapsedTime sequenceTimer = new ElapsedTime();
    private int currentStep = 0;
    private double lastVelocity = 0;

    @Override
    public void initTele() {
        gpA.onClick(Button.B, () -> {
            shooterActive = !shooterActive;
            sequenceRunning = false;
            currentStep = 0;
            if (!shooterActive) {
                turret.shooter.setTargetVelocity(0);
                turret.turn(0);
            }
        });

        // TUNE P (D-pad) | TUNE SUCCESSION (Bumpers)
        gpA.onClick(Button.DPAD_UP, () -> p += 5.0);
        gpA.onClick(Button.DPAD_DOWN, () -> p -= 5.0);
        gpA.onClick(Button.RIGHT_BUMPER, () -> successionCoeff += 0.01);
        gpA.onClick(Button.LEFT_BUMPER, () -> successionCoeff -= 0.01);
    }

    @Override
    public void loopTele() {
        intake.intakeAndFeed(gpA.rt);

        // 1. TURRET TARGETING (Original Qbit Math)
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

        // 2. SHOOTER VELOCITY & DETECTION
        double actual = turret.shooter.getVelocity();
        double baseTarget = shooterActive ? turret.getShooterRPMFromLimelight() * Turret.SHOOT_RATIO_1 : 0;

        // KICKSTART DETECTION: Detect first ball to start the timer
        if (shooterActive && !sequenceRunning && (lastVelocity - actual) > 140) {
            sequenceRunning = true;
            sequenceTimer.reset();
        }
        lastVelocity = actual;

        // 3. THE TIMED SEQUENCE (Post-Kickstart)
        double finalTarget = baseTarget;

        if (sequenceRunning) {
            double time = sequenceTimer.seconds();

            if (time < stepTime) {
                currentStep = 1; // Powering up for Ball 2
            } else if (time < stepTime * 2) {
                currentStep = 2; // Powering up for Ball 3
            } else {
                // Done with 3 balls, return to base speed
                sequenceRunning = false;
                currentStep = 0;
            }
            finalTarget = baseTarget * (1.0 + (currentStep * successionCoeff));
        }

        // Safety cap to prevent 4000 RPM spikes
        if (finalTarget > 3600) finalTarget = 3600;

        turret.shooter.setPIDF(p, 0, 0, f);
        turret.shooter.setTargetVelocity(shooterActive ? finalTarget : 0);

        // --- TELEMETRY ---
        display("Mode", !shooterActive ? "IDLE" : (sequenceRunning ? "BLITZING" : "AWAITING BALL 1"));
        display("Step", (currentStep + 1) + " / 3");
        display("P Value", p);
        display("Ratio Coeff", String.format("%.2f", successionCoeff));
        display("Actual/Target", (int)actual + " / " + (int)finalTarget);
    }
}