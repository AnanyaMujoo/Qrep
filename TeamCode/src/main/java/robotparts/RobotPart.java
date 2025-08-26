package robotparts;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.ArrayList;

import global.Common;
import robotparts.electronics.Electronic;
import robotparts.electronics.Motor;
import robotparts.electronics.MotorWithEncoder;

public abstract class RobotPart implements Common {

    private final ArrayList<Electronic> electronics;

    public abstract void init();
    public RobotPart(){
        electronics = new ArrayList<>();
        allRobotParts.get().add(this);
    }
//    private Electronic createFromType(String name, ElectronicType type) {
//        switch (type) {
//            case CMOTOR_FORWARD:
//                return new CMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            case CMOTOR_REVERSE:
//                return new CMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.BRAKE, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            case CMOTOR_FORWARD_FLOAT:
//                return new CMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            case CMOTOR_REVERSE_FLOAT:
//                return new CMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.FLOAT, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            case CSERVO_FORWARD:
//                return new CServo(hardwareMap.get(CRServo.class, name), DcMotorSimple.Direction.FORWARD);
//            case CSERVO_REVERSE:
//                return new CServo(hardwareMap.get(CRServo.class, name), DcMotorSimple.Direction.REVERSE);
//            case PMOTOR_FORWARD:
//                return new PMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            case PMOTOR_REVERSE:
//                return new PMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.BRAKE, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            case PMOTOR_FORWARD_FLOAT:
//                return new PMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            case PMOTOR_REVERSE_FLOAT:
//                return new PMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.FLOAT, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            case PSERVO_FORWARD:
//                return new PServo(hardwareMap.get(Servo.class, name), Servo.Direction.FORWARD);
//            case PSERVO_REVERSE:
//                return new PServo(hardwareMap.get(Servo.class, name), Servo.Direction.REVERSE);
//            case ICAMERA_EXTERNAL:
//                return new ICamera(hardwareMap.get(WebcamName.class, name), ICamera.CameraType.EXTERNAL, OpenCvCameraRotation.UPSIDE_DOWN);
//            case ICAMERA_INTERNAL:
//                return new ICamera(ICamera.CameraType.INTERNAL, OpenCvCameraRotation.UPRIGHT);
//            case ICOLOR:
//                return new IColor(hardwareMap.get(ColorRangeSensor.class, name));
//            case IDISTANCE:
//                return new IDistance(hardwareMap.get(DistanceSensor.class, name));
//            case IENCODER_NORMAL:
//                return new IEncoder(hardwareMap.get(DcMotor.class, IEncoder.getMotorName(name)), IEncoder.EncoderType.NORMAL);
//            case IENCODER_PMOTOR:
//                return new IEncoder(hardwareMap.get(DcMotor.class, IEncoder.getMotorName(name)), IEncoder.EncoderType.PMOTOR);
//            case IENCODER_CMOTOR:
//                return new IEncoder(hardwareMap.get(DcMotor.class, IEncoder.getMotorName(name)), IEncoder.EncoderType.CMOTOR);
//            case IGYRO:
//                return new IGyro(hardwareMap.get(IMU.class, name));
//            case ITOUCH:
//                return new ITouch(hardwareMap.get(TouchSensor.class, name));
//            case OLED:
//                return new OLed(hardwareMap.get(DigitalChannel.class, "g" + name), hardwareMap.get(DigitalChannel.class, "r" + name));
//            default:
//                fault.check("Electronic creation does not match any known type", Expectation.INCONCEIVABLE, Magnitude.CATASTROPHIC);
//                return null;
//        }
//    }

    private <T extends Electronic> T addElectronic(T electronic){
        electronics.add(electronic);
        return electronic;
    }
    public Motor createMotor(String name, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zp) {
        return addElectronic(new Motor(hardwareMap.get().get(DcMotorEx.class, name), dir, zp));
    }
    public Motor createMotor(String name, DcMotor.Direction dir) {
        return addElectronic(new Motor(hardwareMap.get().get(DcMotorEx.class, name), dir, DcMotor.ZeroPowerBehavior.FLOAT));
    }
    public MotorWithEncoder createMotorWithEncoder(String name, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zpb, boolean invertedEncoder){
        return addElectronic(new MotorWithEncoder(hardwareMap.get().get(DcMotorEx.class, name), dir, zpb, invertedEncoder));
    }
    public MotorWithEncoder createMotorWithEncoder(String name, DcMotor.Direction dir) {
        return addElectronic(new MotorWithEncoder(hardwareMap.get().get(DcMotorEx.class, name), dir, DcMotor.ZeroPowerBehavior.BRAKE, false));
    }

    public void takeAccessFromMainThread(){ electronics.forEach(Electronic::takeAccessFromMainThread); }
    public void returnAccessToMainThread(){ electronics.forEach(Electronic::returnAccessToMainThread); }
    public void stop(){ electronics.forEach(Electronic::stop);}
    public void stopAndReturnAccessToMainThread(){ stop(); returnAccessToMainThread(); }

}
