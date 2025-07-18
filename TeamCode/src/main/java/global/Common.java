package global;

import static global.General.bot;
import static global.General.cameraMonitorViewId;
import static global.General.fault;
import static global.General.gameTime;
import static global.General.gamepad1;
import static global.General.gamepad2;
import static global.General.gph1;
import static global.General.gph2;
import static global.General.hardwareMap;
import static global.General.log;
import static global.General.mainUser;
import static global.General.storage;
import static global.General.sync;
import static global.General.telemetry;
import static global.General.voltageScale;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

import robotparts.RobotPart;
import utility.InitVar;

public interface Common {
//    public static Qbot bot;
    InitVar<HardwareMap> hardwareMap = new InitVar<>();
    InitVar<Telemetry> telemetry = new InitVar<>();
    InitVar<Gamepad> gamepad1 = new InitVar<>();
    InitVar<Gamepad> gamepad2 = new InitVar<>();
    InitVar<ArrayList<RobotPart>> allRobotParts = new InitVar<>();
//    public static ElapsedTime gameTime;
//    public static Fault fault;
//    public static GamepadHandler gph1;
//    public static GamepadHandler gph2;
//    public static Logger log;
//    public static Synchroniser sync;
//    public static Storage storage;
//    public static FieldSide fieldSide = FieldSide.BLUE;
//    public static FieldPlacement fieldPlacement = FieldPlacement.LOWER;
//    public static User mainUser;
//    public static int cameraMonitorViewId;
//    public static double voltageScale;



}