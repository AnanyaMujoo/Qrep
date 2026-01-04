package teleop.testopmodes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teleop.Tele;
import utility.Timer;

@Disabled
@TeleOp
public class TestErrorOp2 extends Tele {

    private final Timer testTimer = new Timer();

    @Override
    public void initTele() {

    }

    @Override
    public void loopTele() {
        double s = testTimer.seconds();
    }
}
