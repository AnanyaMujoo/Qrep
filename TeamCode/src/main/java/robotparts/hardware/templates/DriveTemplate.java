package robotparts.hardware.templates;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import robotparts.RobotPart;
import robotparts.electronics.Motor;


public class DriveTemplate extends RobotPart {

    public Motor frontRight, backRight, frontLeft, backLeft;

    @Override
    public void init() {

        frontRight = createMotor("fr", DcMotorSimple.Direction.REVERSE);
        backRight = createMotor("br", DcMotorSimple.Direction.REVERSE);
        frontLeft = createMotor("fl", DcMotorSimple.Direction.FORWARD);
        backLeft = createMotor("bl", DcMotorSimple.Direction.FORWARD);
    }

    public void move(double forwardPower, double strafePower, double turnPower) {
        frontRight.setPower(forwardPower - strafePower - turnPower);
        backRight.setPower(forwardPower + strafePower - turnPower);
        frontLeft.setPower(forwardPower + strafePower + turnPower);
        backLeft.setPower(forwardPower - strafePower + turnPower);
    }

}
