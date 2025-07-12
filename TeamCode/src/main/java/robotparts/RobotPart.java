package robotparts;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCameraRotation;

import robotparts.electronics.Electronic;

public class RobotPart {


    private Electronic createFromType(String name, ElectronicType type){
        switch (type){
            case CMOTOR_FORWARD:
                return new CMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            case CMOTOR_REVERSE:
                return new CMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.BRAKE, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            case CMOTOR_FORWARD_FLOAT:
                return new CMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            case CMOTOR_REVERSE_FLOAT:
                return new CMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.FLOAT, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            case CSERVO_FORWARD:
                return new CServo(hardwareMap.get(CRServo.class, name), DcMotorSimple.Direction.FORWARD);
            case CSERVO_REVERSE:
                return new CServo(hardwareMap.get(CRServo.class, name), DcMotorSimple.Direction.REVERSE);
            case PMOTOR_FORWARD:
                return new PMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            case PMOTOR_REVERSE:
                return new PMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.BRAKE, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            case PMOTOR_FORWARD_FLOAT:
                return new PMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.FLOAT, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            case PMOTOR_REVERSE_FLOAT:
                return new PMotor(hardwareMap.get(DcMotor.class, name), DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.FLOAT, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            case PSERVO_FORWARD:
                return new PServo(hardwareMap.get(Servo.class, name), Servo.Direction.FORWARD);
            case PSERVO_REVERSE:
                return new PServo(hardwareMap.get(Servo.class, name), Servo.Direction.REVERSE);
            case ICAMERA_EXTERNAL:
                return new ICamera(hardwareMap.get(WebcamName.class, name), ICamera.CameraType.EXTERNAL, OpenCvCameraRotation.UPSIDE_DOWN);
            case ICAMERA_INTERNAL:
                return new ICamera(ICamera.CameraType.INTERNAL, OpenCvCameraRotation.UPRIGHT);
            case ICOLOR:
                return new IColor(hardwareMap.get(ColorRangeSensor.class, name));
            case IDISTANCE:
                return new IDistance(hardwareMap.get(DistanceSensor.class, name));
            case IENCODER_NORMAL:
                return new IEncoder(hardwareMap.get(DcMotor.class, IEncoder.getMotorName(name)), IEncoder.EncoderType.NORMAL);
            case IENCODER_PMOTOR:
                return new IEncoder(hardwareMap.get(DcMotor.class, IEncoder.getMotorName(name)), IEncoder.EncoderType.PMOTOR);
            case IENCODER_CMOTOR:
                return new IEncoder(hardwareMap.get(DcMotor.class, IEncoder.getMotorName(name)), IEncoder.EncoderType.CMOTOR);
            case IGYRO:
                return new IGyro(hardwareMap.get(IMU.class, name));
            case ITOUCH:
                return new ITouch(hardwareMap.get(TouchSensor.class, name));
            case OLED:
                return new OLed(hardwareMap.get(DigitalChannel.class,  "g" + name), hardwareMap.get(DigitalChannel.class,  "r" + name));
            default:
                fault.check("Electronic creation does not match any known type", Expectation.INCONCEIVABLE, Magnitude.CATASTROPHIC);
                return null;
        }
    }
}
