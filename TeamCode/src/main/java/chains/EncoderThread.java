package chains;

import static global.Common.allMotorWithEncoderRotational;
import static global.Common.allMotorWithEncoders;

import robotparts.electronics.Encoder;
import robotparts.electronics.MotorWithEncoder;
import robotparts.electronics.MotorWithEncoderRotational;
import utility.ThreadBase;

public class EncoderThread extends ThreadBase {

    public EncoderThread(double updateRate) {
        super(updateRate);
    }

    @Override
    public void update() throws Exception {
        allMotorWithEncoders.get().forEach(MotorWithEncoder::updateEncoderPosition);
        allMotorWithEncoderRotational.get().forEach(MotorWithEncoderRotational::updateEncoderPosition);
    }
}
