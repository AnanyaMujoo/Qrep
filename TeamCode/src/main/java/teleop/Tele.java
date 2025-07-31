package teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import global.Common;
import global.Initializer;
import robotparts.RobotConfig;
import teleop.teleutil.GamepadHandler;

public abstract class Tele extends OpMode implements Initializer {
    public GamepadHandler gph1;
    public GamepadHandler gph2;
    public abstract void initTele();


    public void startTele() {}

    public abstract void loopTele();

    public void stopTele(){}

    @Override
    public final void init() {
        _init(this);
        gph1 = new GamepadHandler(Common.gamepad1.get());
        gph2 = new GamepadHandler(Common.gamepad2.get());
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
        gph1.updateButtonEvents();
        gph2.updateButtonEvents();
        _loop(true);
    }

    @Override
    public final void stop() {
        stopTele();
        _stop();
    }
}