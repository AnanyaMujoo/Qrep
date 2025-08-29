package teleop.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teleop.Tele;
import utility.ThreadBase;

@TeleOp
public class TestErrorOp extends Tele {

    private ThreadBase testThread;

    @Override
    public void initTele() {
        testThread = new ThreadBase(100) {
            @Override
            public void update() throws Exception {
                throw new ArithmeticException();
            }
        };
    }

    @Override
    public void _start() {
        testThread.start();
    }

    @Override
    public void loopTele() {

    }
}
