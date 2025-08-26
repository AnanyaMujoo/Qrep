package global;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.ArrayList;

import chains.ChainThread;
import robotparts.RobotConfig;
import robotparts.RobotPart;
import utility.AutoInstantiate;
import utility.ThreadBase;

public interface Initializer extends Common{
    default void _init(OpMode thisOpMode) {
        hardwareMap.set(thisOpMode.hardwareMap);
        telemetry.set(thisOpMode.telemetry);
        gamepad1.set(thisOpMode.gamepad1);
        gamepad2.set(thisOpMode.gamepad2);
        allRobotParts.set(new ArrayList<>());
        AutoInstantiate.initializeStaticFields(RobotConfig.class);
        allRobotParts.get().forEach(RobotPart::init);
        chainThread.set(new ChainThread(Constants.CHAIN_THREAD_REFRESH_RATE));
        allThreads.get().forEach(ThreadBase::start);

        //
//        gameTime = new ElapsedTime();
//        gph1 = new GamepadHandler(gamepad1);
//        gph2 = new GamepadHandler(gamepad2);
//        fault = new Fault();
//        sync = new Synchroniser();
//        log = new Logger();
//        mainUser = User.getUserFromTypeOfOpMode(thisOpMode);
//        cameraMonitorViewId = Cameras.getCameraMonitorViewId();
//        voltageScale = RobotFramework.calculateVoltageScale(RobotFramework.getBatteryVoltage());
//        storage = new Storage();
//        bot = new TerraBot();
//        bot.init();
    }

    default void _start() {

//        bot.start();
//        sync.resetDelay();
//        log.clearTelemetry();
    }

    default void _loop(boolean showTelemetry) {
//        bot.update();
//        gph1.run();
//        gph2.run();
//        sync.update();
//        if (showTelemetry) {
//            log.showTelemetry();
//        }
        allThreads.get().forEach(ThreadBase::checkForExceptionAndTellMainThread);
    }

    default void _stop() {
        allThreads.get().forEach(ThreadBase::stopThread);
//        bot.stop();
//        sync.logDelay();
//        log.showLogs();
//        storage.saveItems();
    }
}
