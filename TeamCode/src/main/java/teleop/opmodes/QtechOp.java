package teleop.opmodes;

import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.shooter;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import teleop.Tele;
import teleop.teleutil.Button;

@TeleOp
public class QtechOp extends Tele {

    public static AtomicBoolean readyToShoot = new AtomicBoolean(false);


    @Override
    public void initTele() {
     //prespecificed buttons go here(when they are linked with chains)
        // 1. CONTROL POWER: Use Right Bumper to increase power
        // (Ensure you added the 'increasePower' method to Shooter.java from the previous step)
        shooter.topRight.setPIDF(25, 0, 0.00000, 13.5);
        shooter.topLeft.setPIDF(25, 0, 0.00000, 13.5);

        shooter.topRight.softResetEncoder();
        shooter.topLeft.softResetEncoder();



//
//        gpA.onPress(Button.RIGHT_BUMPER, shooter::increasePower);
//        gpA.onPress(Button.LEFT_BUMPER, shooter::decreasePower);


        gpA.onClick(Button.RIGHT_BUMPER, shooter.setShootTarget(3000.0));
        gpA.onClick(Button.LEFT_BUMPER, shooter.setShootTarget(2000.0));
        gpA.onClick(Button.DPAD_DOWN, shooter.resetShootMode());


        gpA.onClick(Button.Y, Intake);
        gpA.onClick(Button.X, Shoot);

//        gpA.onPress(Button.Y, shooter.shootRunnable(0));
    }


    @Override
    public void loopTele() {
// 1. Display the current power so you can see it changing
//        double current = shooter.topLeft.motor.getCurrent(CurrentUnit.AMPS);
//        display("current", current);
//        display("Shooter Power", shooter.manualPower);

        // 2. Drive controls
        drive.move(gpA.ry, gpA.rx, 0.9 * gpA.lx);

        // 3. Intake controls
        shooter.intake(gpA.lt);
        shooter.intake(-gpA.rt);

        display("Top Right RPM", shooter.topRight.getVelocity());
        display("Top Left RPM", shooter.topLeft.getVelocity());






        // DELETE the old "if (gpA.rt > 0.1)" block entirely.
        // The shooting is now handled by the 'onClickToggle' in initTele above.

//        double st = shooterTarget.get();
//        if(st != oldTarget.get()){
//            if(st != 0) {
//                shooter.topRight.setTargetVelocity(st);
//                shooter.topLeft.setTargetVelocity(st);
//            }else{
//                shooter.topRight.resetRunMode();
//                shooter.topLeft.resetRunMode();
//                shooter.shoot(0.0);
//            }
//            oldTarget.set(st);
//        }

//        display("target", shooterTarget.get());

    }
}

