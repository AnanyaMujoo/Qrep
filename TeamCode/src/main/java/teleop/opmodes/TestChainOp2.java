package teleop.opmodes;

import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.encoderTest;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Locale;

import teleop.Tele;
import teleop.teleutil.Button;
import utility.MainThreadAccess;

@TeleOp
public class TestChainOp2 extends Tele {

    @Override
    public void initTele() {
        encoderTest.softResetEncoder();
        gpA.onClick(Button.Y, Test2);
//        gpA.onClick(Button.X, drive::takeAccessFromMainThread);
//        stage.timer.reset();
    }

    @Override
    public void loopTele() {
//        display("Loop Condition", stage.loopCondition.get());
//        display("Cancel Requested", stage.cancelRequested.get());
        drive.move(gpA.ry, gpA.rx, gpA.lx);
        display("Motor Position", "%.2f cm", encoderTest.getPosition());
//        display("Holding Position", encoderTest.motor.positionHolder.holdingPosition.get());
        display("Current Position", encoderTest.motor.encoder.motor.getCurrentPosition());
        display("Target Position", encoderTest.motor.encoder.motor.getTargetPosition());
        display("Mode", encoderTest.motor.encoder.motor.getMode());

//        display("Main thread", MainThreadAccess.isRunningFromMainThread());
//        display("Main thread acesss", drive.backLeft.access.mainThreadAccess.get());
//        display("Drive access", drive.backLeft.access.doesCurrentThreadHaveAccess());
    }
}
