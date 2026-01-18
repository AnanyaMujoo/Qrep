package robotparts.hardware;

import robotparts.RobotPart;
import robotparts.electronics.Motor;

public class Shooter extends RobotPart {

    public Motor topRight, topLeft, bottomRight, bottomLeft;

    @Override
    public void init() {
        topRight = createMotor("topr", MOTOR_REVERSE, MOTOR_FLOAT);
        topLeft = createMotor("topl", MOTOR_FORWARD, MOTOR_FLOAT);
        bottomRight = createMotor("botr", MOTOR_REVERSE, MOTOR_FLOAT);
        bottomLeft = createMotor("botl", MOTOR_FORWARD, MOTOR_FLOAT);


    }

    public void intake(double forwardPower) {
        bottomRight.setPower(forwardPower);
        bottomLeft.setPower(forwardPower);

    }

    public void shoot(double forwardPower) {
        topRight.setPower(forwardPower);
        topLeft.setPower(forwardPower);
    }

    public void shootAndIntake(double powerIntake, double powerShooter) {
        intake(powerIntake);
        shoot(powerShooter);
    }

    public Runnable intakeRunnable(double forwardPower) {
        return () -> intake(forwardPower);
    }

    public Runnable shootRunnable(double forwardPower) {
        return () -> intake(forwardPower);
    }

    public Runnable shootAndIntakeRunnable(double powerIntake, double powerShooter) {
        return () -> shootAndIntake(powerIntake, powerShooter);
    }

    // 1. Add a variable to store the manual power (Default 0.5 or 50%)
    public double manualPower = 0.5;

    private long lastPressTime = 0;

    public void increasePower() {
        // 2. CHECK TIME: If less than 0.25 seconds have passed, ignore this click
        if (System.currentTimeMillis() - lastPressTime < 250) {
            return;
        }
        // Update the last press time
        lastPressTime = System.currentTimeMillis();

        // 3. INCREASE POWER
        manualPower += 0.1;

        // 4. ROUND IT: This forces 0.700001 to become exactly 0.7
        manualPower = Math.round(manualPower * 10.0) / 10.0;

        // 5. WRAP AROUND
        if (manualPower > 1.0) {
            manualPower = 0;
        }
        shoot(-manualPower);
    }

    public void decreasePower() {
        // 2. CHECK TIME: If less than 0.25 seconds have passed, ignore this click
        if (System.currentTimeMillis() - lastPressTime < 250) {
            return;
        }
        // Update the last press time
        lastPressTime = System.currentTimeMillis();

        // 3. DECREASE POWER
        manualPower -= 0.1;

        // 4. ROUND IT: This forces 0.700001 to become exactly 0.7
        manualPower = Math.round(manualPower * 10.0) / 10.0;

        // 5. WRAP AROUND
        if (manualPower > 1.0) {
            manualPower = 0;
        }
        shoot(-manualPower);
    }

    public void zeroPower() {
        // 2. CHECK TIME: If less than 0.25 seconds have passed, ignore this click
        if (System.currentTimeMillis() - lastPressTime < 250) {
            return;
        }
        // Update the last press time
        lastPressTime = System.currentTimeMillis();

        // 3. set 0 POWER
        manualPower = 0;

        // 4. ROUND IT: This forces 0.700001 to become exactly 0.7
        manualPower = Math.round(manualPower * 10.0) / 10.0;

        // 5. WRAP AROUND
        if (manualPower > 1.0) {
            manualPower = 0;
        }
        shoot(manualPower);
    }
}
