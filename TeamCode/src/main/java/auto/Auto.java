package auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import chains.Stage;
import global.Initializer;
import utility.Timer;

public abstract class Auto extends LinearOpMode implements Initializer, AutoChain{

    public abstract void initAuto();
    public abstract void runAuto();
    public void stopAuto() {}

    @Override
    public final void runOpMode() throws InterruptedException {
        _init(this);
        initAuto();
        waitForStart();
        _start();
        runAuto();
        stopAuto();
        _stop();
    }




}
