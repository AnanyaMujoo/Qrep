package teleop.opmodes;

import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.shooter;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teleop.Tele;
import teleop.teleutil.Button;

@Disabled
@TeleOp
public class QtechOp extends Tele {




    @Override
    public void initTele() {
//prespecificed buttons go here(when they are linked with chains)
        gpA.onPress(Button.B, shootFar);
        //TODO make a way for shooting manually from not identified positions
        // maybe use the bumper to increase the speed by increments, also display reccomended positions/distances for the speed, and current setting



    }


    @Override
    public void loopTele() {



//        display("A: Press Mode");
//        drive.move(0.2,0.3,0.4);
        drive.move(gpA.ry, gpA.rx, gpA.ly);
        shooter.intake(gpA.lt);
        shooter.shoot(gpA.rt);



    }
}
