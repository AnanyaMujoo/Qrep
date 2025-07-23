package teleop.opmodes;
import static robotparts.RobotConfig.drive;
import robotparts.hardware.Drive;
import teleop.Tele;

public class TestOp extends Tele {

    @Override
    public void initTele() {
    }

    @Override
    public void loopTele() {
        drive.move(0.2,0.3,0.4);

    }
}
