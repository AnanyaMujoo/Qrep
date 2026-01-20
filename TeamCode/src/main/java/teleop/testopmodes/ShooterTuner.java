package teleop.testopmodes;

import static robotparts.RobotConfig.turret;
import static robotparts.RobotConfig.intake;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import geometry.Pose;
import robotparts.hardware.Turret;
import teleop.Tele;
import teleop.teleutil.Button;

@TeleOp(name = "ShooterLockOnTest", group = "Tuning")
public class ShooterTuner extends Tele {

    // --- TUNING VARIABLES ---
    private double p = 14.0;
    private double f = 14.5;

    // --- TEST VARIABLES ---
    private boolean useTargeting = false;
    private double manualRPM = 1500;

    // --- EXACT QBIT LOGIC VARIABLES ---
    // These mimic the AtomicReferences in QbitOp
    private double currentShootError = 0;
    private double currentTurnError = 0;
    private ElapsedTime stabilityTimer = new ElapsedTime();

    // --- MENU STATE ---
    private int editMode = 0;
    private String editName = "F (Feedforward)";
    private double multiplier = 0.5;

    @Override
    public void initTele() {
        gpA.onClick(Button.Y, () -> { editMode = 0; editName = "F (Feedforward)"; multiplier = 0.5; });
        gpA.onClick(Button.X, () -> { editMode = 1; editName = "P (Proportional)"; multiplier = 1.0; });
        gpA.onClick(Button.A, () -> { editMode = 2; editName = "Manual RPM"; multiplier = 50.0; });

        gpA.onClick(Button.DPAD_UP, () -> adjustValue(1));
        gpA.onClick(Button.DPAD_DOWN, () -> adjustValue(-1));

        // Toggle Limelight Logic
        gpA.onClick(Button.LEFT_BUMPER, () -> {
            useTargeting = !useTargeting;
            stabilityTimer.reset();
        });

        // Stop
        gpA.onClick(Button.B, () -> { manualRPM = 0; turret.shooter.setTargetVelocity(0); });

        turret.shooter.softResetEncoder();
        display("Status", "Ready.");
    }

    private void adjustValue(int dir) {
        if(editMode == 0) f = Math.max(0, f + (dir * multiplier));
        if(editMode == 1) p = Math.max(0, p + (dir * multiplier));
        if(editMode == 2) manualRPM += (dir * multiplier);
    }

    @Override
    public void loopTele() {
        // 1. INTAKE (Direct Control)
        intake.intakeAndFeed(gpA.rt);

        // 2. UPDATE PIDF
        turret.shooter.setPIDF(p, 0, 0, f);

        // 3. TARGETING LOGIC (COPIED FROM QBITOP)
        double targetRPM = manualRPM;
        double turnPower = 0;

        // Reset errors for this loop
        currentTurnError = 0;

        if (useTargeting) {
            Pose pose = turret.getPoseWithLimey();
            double distance = pose.getY();

            if (distance > 0) {
                // --- TURNING MATH (QbitOp) ---
                double angle = pose.getAngle();
                double targetAngle = Math.toDegrees(Math.atan(Turret.LIMEY_LEFT_DISTANCE/distance));
                double error = targetAngle - angle;

                // Save this error for the logic check later
                currentTurnError = error;

                // Turn Power Calc
                turnPower = (-Math.signum(error)*Turret.TURRET_TARGETING_REST_POWER - error*Turret.TURRET_TARGETING_K)*0.5;

                // --- SHOOTER MATH (QbitOp) ---
                targetRPM = turret.getShooterRPMFromLimelight() * Turret.SHOOT_RATIO_1;
            }
        }

        // Apply Motor Powers
        if(Math.abs(currentTurnError) > 1) {
            turret.turn(turnPower);
        } else {
            turret.turn(0.0);
        }
        turret.shooter.setTargetVelocity(targetRPM);

        // 4. "IS READY" LOGIC (COPIED FROM TURRET.JAVA)
        // Logic: if(turnError > 2 || shootError > 80) reset(); else return timer > 0.4

        currentShootError = Math.abs(targetRPM - turret.shooter.getVelocity());
        double absTurnError = Math.abs(currentTurnError);

        boolean isBad = (absTurnError > 2.0) || (currentShootError > 80.0);

        if (isBad) {
            stabilityTimer.reset(); // Reset if we fail EITHER condition
        }

        boolean systemReady = !isBad && (stabilityTimer.seconds() > 0.4);

        // 5. DISPLAY
        PIDFCoefficients real = turret.shooter.getPIDF();

        display("MODE", useTargeting ? "AUTO (Limelight)" : "MANUAL");
        display("EDITING", editName);

        display("----- STATUS -----", "-");
        if (systemReady) {
            display("SYSTEM READY?", "YES !!!");
            display("Hold Time", stabilityTimer.seconds() + "s");
        } else {
            display("SYSTEM READY?", "NO");
            // Show exactly why it failed
            if (currentShootError > 80) display("FAIL REASON", "RPM Error: " + (int)currentShootError + " > 80");
            else if (absTurnError > 2) display("FAIL REASON", "Turn Error: " + String.format("%.1f", absTurnError) + " > 2");
            else display("FAIL REASON", "Stabilizing... " + String.format("%.2f", stabilityTimer.seconds()) + "s");
        }

        display("----- DATA -----", "-");
        display("Target RPM", targetRPM);
        display("Actual RPM", turret.shooter.getVelocity());
        display("P / F", p + " / " + f);
    }
}