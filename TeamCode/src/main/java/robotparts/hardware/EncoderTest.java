package robotparts.hardware;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

import robotparts.RobotPart;
import robotparts.electronics.MotorWithEncoder;

public class EncoderTest extends RobotPart {

    public MotorWithEncoder motor;

    @Override
    public void init() {
        motor = createMotorWithEncoder("br", FORWARD, BRAKE, false,
                -0.05, 0.5, 1.0,
                3.0, 1, 30);

    }

}
