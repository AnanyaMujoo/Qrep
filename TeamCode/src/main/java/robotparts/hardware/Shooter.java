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

    public Runnable intakeRunnable(double forwardPower){
        return () -> intake(forwardPower);
    }

    public Runnable shootRunnable(double forwardPower){
        return () -> intake(forwardPower);
    }

}
