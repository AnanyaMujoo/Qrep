package robotparts.hardware;

import static global.Modes.driveMode;

import robotparts.RobotPart;
import robotparts.electronics.ElectronicType;
import robotparts.electronics.Motor;
import robotparts.electronics.continuous.CMotor;
import util.codeseg.ReturnCodeSeg;
import util.template.Mode;

public class DriveTemplate extends RobotPart {

    public Motor frontRight, backRight, frontLeft, backLeft;

//    public boolean machineMode = false;


    @Override
    public void init() {

        frontRight = create("fr", ElectronicType.CMOTOR_REVERSE_FLOAT);
        backRight = create("br", ElectronicType.CMOTOR_REVERSE_FLOAT);
        frontLeft = create("fl", ElectronicType.CMOTOR_FORWARD_FLOAT);
        backLeft = create("bl", ElectronicType.CMOTOR_FORWARD_FLOAT);
    }


    @Override
    public void move(double forwardPower, double strafePower, double turnPower) {
        frontRight.setPower(forwardPower - strafePower - turnPower);
        backRight.setPower(forwardPower + strafePower - turnPower);
        frontLeft.setPower(forwardPower + strafePower + turnPower);
        backLeft.setPower(forwardPower - strafePower + turnPower);
    }


}
