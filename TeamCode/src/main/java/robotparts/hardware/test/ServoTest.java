package robotparts.hardware.test;

import robotparts.RobotPart;
import robotparts.electronics.PositionalServo;

public class ServoTest extends RobotPart {

    public PositionalServo servo;

    @Override
    public void init() {
        servo = createPositionalServo("s", SERVO_FORWARD);
    }

    public Runnable servoStartRunnable = () -> servo.moveTo(0.0);
    public Runnable servoEndRunnable = () -> servo.moveTo(1.0);

}
