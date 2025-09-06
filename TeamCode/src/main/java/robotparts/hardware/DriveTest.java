package robotparts.hardware;

import java.util.function.Supplier;

import robotparts.RobotPart;
import robotparts.electronics.Motor;

public class DriveTest extends RobotPart {

    public Motor frontRight, frontLeft, backLeft;

    @Override
    public void init() {
        frontRight = createMotor("fr", MOTOR_REVERSE, MOTOR_FLOAT);
        frontLeft = createMotor("fl", MOTOR_FORWARD, MOTOR_FLOAT);
        backLeft = createMotor("bl", MOTOR_FORWARD, MOTOR_FLOAT);
    }

    public void move(double forwardPower, double strafePower, double turnPower) {
        frontRight.setPower(forwardPower - strafePower - turnPower);
        frontLeft.setPower(forwardPower + strafePower + turnPower);
        backLeft.setPower(forwardPower - strafePower + turnPower);
    }

    public Runnable moveRunnable(double forwardPower, double strafePower, double turnPower){
        return () -> move(forwardPower, strafePower, turnPower);
    }
}
