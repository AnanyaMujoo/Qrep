package global;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import elements.FieldSide;
import elements.Mode;
import teleop.teleutil.GamepadHandler;

public class General {
    /**
     *  Terrabot object, used for moving the robot, and other features related to the robot
     */
    /**
     * Hardware map object, has all of the hardware in the robot like the DcMotors and Servos
     */
    public static HardwareMap hardwareMap;
    /**
     * Telemetry object, used to display output to the phone (like system.out.print())
     */
    public static Telemetry telemetry;
    /**
     * Gamepad objects (controllers), there are two, one is conected using start+A and the other with start+B
     */
    public static Gamepad gamepad1;
    public static Gamepad gamepad2;
    /**
     * Elapsed timer (a timer) object that stores the gametimer, is reset on init
     */
    public static ElapsedTime gameTime;
    /**
     * Fault object (used to throw exceptions and raise warnings
     */
    /**
     * Gamepad handlers, used to make using the gamepads easier
     */
    public static GamepadHandler gph1;

    public static GamepadHandler gph2;
    /**
     * Logger object to store logs
     * NOTE: In most instances use logger instead of telemetry raw
    /**
     * FieldSide object to represent which side of the field we are on (red or blue)
     */
    public static FieldSide fieldSide = FieldSide.BLUE;

    public static Mode mode =  Mode.NO_SCALER;
    /**
     * FieldPlacement object to represent which location of the field we are on (upper [closer to audience] or lower)
     */
    /**
     * Camera Monitor View ID, used for camera viewing
     */
    public static int cameraMonitorViewId;
    /**
     * Inversely proportional to the voltage that the robot started at (used to adjust values)
     */
    public static double voltageScale;
}