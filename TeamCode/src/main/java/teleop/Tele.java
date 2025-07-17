package teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import global.Common;
import global.Initializer;

public abstract class Tele extends OpMode implements Initializer {
    public abstract void initTele();

    public void startTele() {}

    public abstract void loopTele();

    public void stopTele(){}

    @Override
    public final void init() {
        _init(this);
        initTele();
    }

    @Override
    public final void start(){
        _start();
        startTele();
    }

    @Override
    public final void loop() {
        loopTele();
        _loop(true);
    }

    @Override
    public final void stop() {
        stopTele();
        _stop();
    }
}