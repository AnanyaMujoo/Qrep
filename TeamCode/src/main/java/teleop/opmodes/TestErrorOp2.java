package teleop.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teleop.Tele;
import utility.ThreadBase;
import utility.Timer;

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
