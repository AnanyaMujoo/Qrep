package global;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

import chains.ChainThread;
import robotparts.RobotPart;
import utility.InitVar;
import utility.ThreadBase;

public interface Common {
//    public static Qbot bot;
    InitVar<HardwareMap> hardwareMap = new InitVar<>();
    InitVar<Telemetry> telemetry = new InitVar<>();
    InitVar<Gamepad> gamepad1 = new InitVar<>();
    InitVar<Gamepad> gamepad2 = new InitVar<>();
    InitVar<ArrayList<RobotPart>> allRobotParts = new InitVar<>();
    InitVar<ArrayList<ThreadBase>> allThreads = new InitVar<>();
    InitVar<Thread> mainThread = new InitVar<>();
    InitVar<ChainThread> chainThread = new InitVar<>();


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