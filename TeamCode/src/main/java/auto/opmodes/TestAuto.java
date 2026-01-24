package auto.opmodes;

import static robotparts.RobotConfig.drive;
//import static teleop.TeleChain.shootClose;
//import static teleop.TeleChain.shootFar;
//import static teleop.TeleChain.shootManual;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import auto.Auto;
import chains.ChainMaker;
import utility.Timer;

@Autonomous
public class TestAuto extends Auto {
    Timer timer = new Timer();

    public void driveTime(double forwardPower, double strafePower, double turnPower, double time) {
        timer.reset();
        while (opModeIsActive() && timer.seconds() < time){
            drive.move(forwardPower, strafePower, turnPower);
        }
        drive.move(0,0,0);
    }

    public void addChain(ChainMaker chain) {
        chain.run();
    }

    @Override
    public void initAuto() {
        driveTime(0,0,0,0);
        //make drive to where you want to go

//        addChain(shootFar);
        //make a chain for intake, for shooting
        //lmk what you want it to do and ill tell you how to make it



    }

    @Override
    public void runAuto() {

    }
}
