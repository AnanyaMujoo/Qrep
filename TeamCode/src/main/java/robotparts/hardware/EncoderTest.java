package robotparts.hardware;

import robotparts.RobotPart;
import robotparts.electronics.MotorWithEncoder;

public class EncoderTest extends RobotPart {

    public MotorWithEncoder motor;

    @Override
    public void init() {
        motor = createMotorWithEncoder("br", MOTOR_FORWARD, MOTOR_BRAKE, false,
                3.0, 1.0, 30,
                -0.05, 0.5, 1.0);

    }

}
