package robotparts.hardware.templates;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import robotparts.RobotPart;
import robotparts.electronics.Motor;


public class DriveTemplate extends RobotPart {

    public Motor frontRight, backRight, frontLeft, backLeft;

    @Override
    public void init() {

        frontRight = createMotor("fr", REVERSE);
        backRight = createMotor("br", REVERSE);
        frontLeft = createMotor("fl", FORWARD);
        backLeft = createMotor("bl", FORWARD);
    }

    public void move(double forwardPower, double strafePower, double turnPower) {
        frontRight.setPower(forwardPower - strafePower - turnPower);
        backRight.setPower(forwardPower + strafePower - turnPower);
        frontLeft.setPower(forwardPower + strafePower + turnPower);
        backLeft.setPower(forwardPower - strafePower + turnPower);
    }

}
