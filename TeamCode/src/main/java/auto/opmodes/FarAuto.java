package auto.opmodes;

import static robotparts.RobotConfig.drive;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import auto.Auto;
import utility.Timer;

@Autonomous
public class FarAuto extends Auto {

    Timer timer = new Timer();


    @Override
    public void initAuto() {
        timer.reset();

    }

    @Override
    public void runAuto() {
        timer.reset();
        drive.move(0.5, 0, 0);
        while (opModeIsActive() && timer.seconds() < 1){

        }
        drive.move(0,0,0);

    }
}
