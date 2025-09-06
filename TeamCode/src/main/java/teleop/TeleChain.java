package teleop;

import static chains.StageBuilder.stage;
import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.encoderTest;
import static robotparts.RobotConfig.servoTest;

import java.util.function.Supplier;

import chains.Chain;
import chains.ChainMaker;
import chains.Stage;

public interface TeleChain {

    ChainMaker Test2 = () -> new Chain(
            stage(drive, drive.moveRunnable(0.1, 0.2, 0.3), 1),
            stage(encoderTest.setTargetRunnable(10, 0.5), 3),
            stage(servoTest.servoStartRunnable),
            stage(encoderTest.setTargetRunnable(20, 0.5), encoderTest.isAtTargetSupplier),
            stage(servoTest.servoEndRunnable, 3),
            stage(encoderTest.setTargetRunnable(0, 0.5)),
            stage(servoTest.servoStartRunnable),
            stage(3)
    );

}