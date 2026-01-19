package robotparts;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

import global.Common;
import robotparts.electronics.ColorSensor;
import robotparts.electronics.Electronic;
import robotparts.electronics.Motor;
import robotparts.electronics.MotorWithEncoder;
import robotparts.electronics.MotorWithEncoderRotational;
import robotparts.electronics.PositionalServo;
import robotparts.electronics.PositionalServoGroup;

public abstract class RobotPart implements Common {

    protected final DcMotorSimple.Direction MOTOR_REVERSE = DcMotorSimple.Direction.REVERSE;
    protected final DcMotorSimple.Direction MOTOR_FORWARD = DcMotorSimple.Direction.FORWARD;
    protected final DcMotor.ZeroPowerBehavior MOTOR_BRAKE = DcMotor.ZeroPowerBehavior.BRAKE;
    protected final DcMotor.ZeroPowerBehavior MOTOR_FLOAT = DcMotor.ZeroPowerBehavior.FLOAT;

    protected final Servo.Direction SERVO_REVERSE = Servo.Direction.REVERSE;
    protected final Servo.Direction SERVO_FORWARD = Servo.Direction.FORWARD;


    private final ArrayList<Electronic> electronics;

    public abstract void init();

    public RobotPart(){
        electronics = new ArrayList<>();
        allRobotParts.get().add(this);
    }

    private <T extends Electronic> T addElectronic(T electronic){
        electronics.add(electronic);
        return electronic;
    }
    public Motor createMotor(String name, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zp) {
        return addElectronic(new Motor(QhardwareMap.get().get(DcMotorEx.class, name), dir, zp));
    }



    public MotorWithEncoder createMotorWithEncoder
            (String name, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zpb,
             boolean invertedEncoder, double pulleyRadius, double motorToPulleyGearRatio,
             double maximumDistance, double snapToZeroPower, double snapToZeroTime,
             double snapToZeroDistance){
        return addElectronic(new MotorWithEncoder(QhardwareMap.get().get(DcMotorEx.class, name),
                dir, zpb, invertedEncoder, pulleyRadius, motorToPulleyGearRatio, maximumDistance,
                snapToZeroPower, snapToZeroTime, snapToZeroDistance));
    }

    public MotorWithEncoderRotational createMotorWithEncoderRotational
            (String name, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zpb,
             boolean invertedEncoder,double encoderTicksPerRevolution, double motorToOutputGearRatio){
        return addElectronic(new MotorWithEncoderRotational(QhardwareMap.get().get(DcMotorEx.class, name),
                dir, zpb, invertedEncoder, encoderTicksPerRevolution, motorToOutputGearRatio));
    }

    public PositionalServo createPositionalServo(String name, Servo.Direction dir){
        return addElectronic(new PositionalServo(QhardwareMap.get().get(Servo.class, name), dir));
    }

    public PositionalServoGroup createPositionalServoGroup(String name1, Servo.Direction dir1, String name2, Servo.Direction dir2){
        return new PositionalServoGroup(createPositionalServo(name1, dir1), createPositionalServo(name2, dir2));
    }

    public ColorSensor createColorSensor(String name){
        return addElectronic(new ColorSensor(QhardwareMap.get().get(NormalizedColorSensor.class, name)));
    }

    public void takeAccessFromMainThread(){ electronics.forEach(Electronic::takeAccessFromMainThread); }
    public void returnAccessToMainThread(){ electronics.forEach(Electronic::returnAccessToMainThread); }
    public void stop(){ electronics.forEach(Electronic::stop);}


}
