package teleop.opmodes;

import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.shooter;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

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

        // 2. SHOOT: Use Button A as an ON/OFF Toggle
        // The syntax '() ->' fixes your red line error!
        gpA.onClickToggle(Button.LEFT_BUMPER,
                () -> shooter.shoot(shooter.manualPower),  // Code to run when Toggled ON
                () -> shooter.shoot(0)                     // Code to run when Toggled OFF
        );


    }


    @Override
    public void loopTele() {
// 1. Display the current power so you can see it changing
        display("Shooter Power", shooter.manualPower);

        // 2. Drive controls
        drive.move(0.8 * gpA.ry, 0.8 * gpA.rx, 0.8 * gpA.lx);

        // 3. Intake controls
        shooter.intake(gpA.lt);
        shooter.intake(-gpA.rt);



        // DELETE the old "if (gpA.rt > 0.1)" block entirely.
        // The shooting is now handled by the 'onClickToggle' in initTele above.

    }
}

