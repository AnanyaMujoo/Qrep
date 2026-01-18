package teleop.opmodes;

import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.shooter;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import teleop.Tele;
import teleop.teleutil.Button;

@TeleOp
public class QtechOp extends Tele {


    @Override
    public void initTele() {
     //prespecificed buttons go here(when they are linked with chains)
        // 1. CONTROL POWER: Use Right Bumper to increase power
        // (Ensure you added the 'increasePower' method to Shooter.java from the previous step)
        gpA.onPress(Button.RIGHT_BUMPER, shooter::increasePower);
        gpA.onPress(Button.LEFT_BUMPER, shooter::decreasePower);
        gpA.onPress(Button.Y, shooter.shootRunnable(0));
    }


    @Override
    public void loopTele() {
// 1. Display the current power so you can see it changing
        double current = shooter.topLeft.motor.getCurrent(CurrentUnit.AMPS);
        display("current", current);
        display("Shooter Power", shooter.manualPower);

        // 2. Drive controls
        drive.move(0.9 * gpA.ry, 0.9 * gpA.rx, 0.9 * gpA.lx);

        // 3. Intake controls
        shooter.intake(gpA.lt);
        shooter.intake(-gpA.rt);



        // DELETE the old "if (gpA.rt > 0.1)" block entirely.
        // The shooting is now handled by the 'onClickToggle' in initTele above.

    }
}

