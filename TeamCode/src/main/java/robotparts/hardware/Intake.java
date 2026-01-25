package robotparts.hardware;

import java.util.function.Supplier;

import robotparts.RobotPart;
import robotparts.electronics.ColorSensor;
import robotparts.electronics.Motor;
import robotparts.electronics.MotorWithEncoder;
import robotparts.electronics.MotorWithEncoderRotational;
import robotparts.electronics.PositionalServo;
import utility.Timer;

public class Intake extends RobotPart {

    public Motor intake;
    public MotorWithEncoderRotational feeder;
//    public ColorSensor csb;
    public PositionalServo lock;
    public ColorSensor detect;

    public final Timer timer = new Timer();
    public final double DETECTION_DISTANCE = 5; //less than
    public final double DETECTION_TIME = 0.3; // longer than

    public Supplier<Boolean> isNotDetected = () -> {
        if(getDetectDistance() > DETECTION_DISTANCE){
            timer.reset();
        }else return timer.seconds() < DETECTION_TIME;
        return true;
    };

    @Override
    public void init() {
        intake = createMotor("in", MOTOR_REVERSE, MOTOR_FLOAT);
//        feeder = createMotor("fe", MOTOR_FORWARD, MOTOR_BRAKE);
        feeder = createMotorWithEncoderRotational("fe", MOTOR_FORWARD, MOTOR_BRAKE, false, 383.6,1.0);
//        csb = createColorSensor("cs1");
        lock = createPositionalServo("lo", SERVO_REVERSE);
        detect = createColorSensor("de");
        timer.reset();
    }

    public void intake(double power){
        intake.setPower(power);
    }


    public void feed(double power){
        feeder.setPower(power);
    }

    public void intakeAndFeed(double power){
        intake(power);
        feed(power);
    }



    public Runnable setFeedTargetRelative(double target, double power){
        return () -> {feeder.softResetEncoder(); feeder.setTarget(target, power);};
    }

    public Runnable resetFeedRunMode(){
        return () -> feeder.resetRunMode();
    }
//
//    public double getColorSensorBottomDistance(){
//        return csb.getDistance();
//    }

    public double getDetectDistance(){
        return detect.getDistance();
    }

    public void lock(){
        lock.moveTo(1.0);
    }
//    public void lockTime(double power, double time){lock.move}

    public Supplier<Boolean> isReady = () -> {
        return lock.getPosition() == 1.0;
    };

    public void unlock(){
        lock.moveTo(0.64);
    }
}
