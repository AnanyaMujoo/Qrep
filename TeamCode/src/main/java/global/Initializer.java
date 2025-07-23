package global;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.ArrayList;

import robotparts.RobotConfig;
import utility.AutoInstantiate;

public interface Initializer extends Common{
    default void _init(OpMode thisOpMode) {
        hardwareMap.set(thisOpMode.hardwareMap);
        telemetry.set(thisOpMode.telemetry);
        gamepad1.set(thisOpMode.gamepad1);
        gamepad2.set(thisOpMode.gamepad2);
        allRobotParts.set(new ArrayList<>());
        AutoInstantiate.initializeStaticFields(RobotConfig.class);

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
    }

    default void _stop() {
//        bot.stop();
//        sync.logDelay();
//        log.showLogs();
//        storage.saveItems();
    }
}

//TODO need an interface for some object that acts like an interfae\c
