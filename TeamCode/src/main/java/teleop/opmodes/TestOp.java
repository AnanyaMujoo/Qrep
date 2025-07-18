package teleop.opmodes;
import static robotparts.RobotConfig.drive;
import robotparts.hardware.Drive;
import teleop.Tele;

public class TestOp extends Tele {

    @Override
    public void initTele() {
        drive = new Drive();

        drive.move(2,22111112,22222);
    }

    @Override
    public void loopTele() {

    }
}
