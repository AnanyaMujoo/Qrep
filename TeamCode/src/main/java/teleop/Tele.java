package teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import global.Common;
import global.Initializer;
import teleop.teleutil.GamepadHandler;

public abstract class Tele extends OpMode implements Initializer, TeleChain {
    public GamepadHandler gpA;
    public GamepadHandler gpB;
    public abstract void initTele();


    public void startTele() {}

    public abstract void loopTele();

    public void stopTele(){}

    @Override
    public final void init() {
        _init(this);
        gpA = new GamepadHandler(Common.gamepad1.get());
        gpB = new GamepadHandler(Common.gamepad2.get());
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
        gpA.updateButtonEvents();
        gpB.updateButtonEvents();
        _loop(true);
    }

    @Override
    public final void stop() {
        stopTele();
        _stop();
    }
}