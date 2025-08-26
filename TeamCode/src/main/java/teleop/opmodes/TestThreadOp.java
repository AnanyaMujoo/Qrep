package teleop.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.concurrent.atomic.AtomicBoolean;

import teleop.Tele;
import utility.MainThreadAccess;

@TeleOp
public class TestThreadOp extends Tele {

    private final AtomicBoolean fromMainThread = new AtomicBoolean(false);

    @Override
    public void initTele() {
        fromMainThread.set(true);
    }

    @Override
    public void startTele() {
        new Thread(() -> fromMainThread.set(MainThreadAccess.isRunningFromMainThread())).start();
    }

    @Override
    public void loopTele() {
        display("Main Thread? (false)", fromMainThread.get());
        display("Main Thread? (true)", MainThreadAccess.isRunningFromMainThread());
        display("Main Thread Name", mainThread.get().getName());
    }
}
