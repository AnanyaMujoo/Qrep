package robotparts.hardware;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

import robotparts.RobotPart;
import robotparts.electronics.MotorWithEncoder;

public class EncoderTest extends RobotPart {

    public MotorWithEncoder motor;

    @Override
    public void init() {
        motor = createMotorWithEncoder("br", REVERSE, false)
                .setParameters(0.0, 1.0, 3.0, 1);

    }

}
