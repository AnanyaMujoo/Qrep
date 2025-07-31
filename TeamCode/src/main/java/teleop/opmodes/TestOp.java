package teleop.opmodes;
import static robotparts.RobotConfig.drive;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import robotparts.hardware.Drive;
import teleop.Tele;
import teleop.teleutil.Button;

@TeleOp(name="TestOp")
public class TestOp extends Tele {
    @Override
    public void initTele() {
        gph1.pressMode(Button.A, () -> drive.move(0.1,0.3,0.4));
    }


    @Override
    public void loopTele() {
        drive.move(0.2,0.3,0.4);


    }
}
