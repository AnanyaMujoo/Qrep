package global;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

import chains.ChainThread;
import chains.EncoderThread;
import robotparts.RobotPart;
import robotparts.electronics.MotorWithEncoder;
import robotparts.electronics.MotorWithEncoderRotational;
import utility.InitVar;
import utility.ThreadBase;

public interface Common {
//    public static Qbot bot;
    InitVar<HardwareMap> QhardwareMap = new InitVar<>();
    InitVar<Telemetry> Qtelemetry = new InitVar<>();
    InitVar<Gamepad> gamepad1 = new InitVar<>();
    InitVar<Gamepad> gamepad2 = new InitVar<>();
    InitVar<ArrayList<RobotPart>> allRobotParts = new InitVar<>();
    InitVar<ArrayList<MotorWithEncoder>> allMotorWithEncoders = new InitVar<>();
    InitVar<ArrayList<MotorWithEncoderRotational>> allMotorWithEncoderRotational = new InitVar<>();
    InitVar<ArrayList<ThreadBase>> allThreads = new InitVar<>();
    InitVar<Thread> mainThread = new InitVar<>();
    InitVar<ChainThread> chainThread = new InitVar<>();
    InitVar<EncoderThread> encoderThread = new InitVar<>();


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