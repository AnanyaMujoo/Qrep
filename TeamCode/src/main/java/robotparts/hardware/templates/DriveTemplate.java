package robotparts.hardware.templates;
import robotparts.RobotPart;
import robotparts.electronics.Motor;


public class DriveTemplate extends RobotPart {

    public Motor frontRight, backRight, frontLeft, backLeft;

    @Override
    public void init() {
        frontRight = createMotor("fr", MOTOR_REVERSE, MOTOR_FLOAT);
        backRight = createMotor("br", MOTOR_REVERSE, MOTOR_FLOAT);
        frontLeft = createMotor("fl", MOTOR_FORWARD, MOTOR_FLOAT);
        backLeft = createMotor("bl", MOTOR_FORWARD, MOTOR_FLOAT);
    }

    public void move(double forwardPower, double strafePower, double turnPower) {
        frontRight.setPower(forwardPower - strafePower - turnPower);
        backRight.setPower(forwardPower + strafePower - turnPower);
        frontLeft.setPower(forwardPower + strafePower + turnPower);
        backLeft.setPower(forwardPower - strafePower + turnPower);
    }

}
