package robotparts.hardware;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import elements.FieldSide;
import static global.General.fieldSide;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import geometry.Pose;
import geometry.Vector;
import robotparts.RobotPart;
import robotparts.electronics.Motor;
import robotparts.electronics.MotorWithEncoderRotational;
import teleop.opmodes.QbitOp;
import utility.Timer;

public class Turret extends RobotPart {

    public Motor turret;
    public MotorWithEncoderRotational shooter;
    public Timer timer = new Timer();
    public Timer timer2 = new Timer();

    public double currentPower = 0;

    public Limelight3A limey;
    public IMU imu;

    public static final double MOUNT_ANGLE = 15;
    public static final double HEIGHT_DIFFERENCE = 38 + 40;
    public static final double LIMEY_LEFT_DISTANCE = 12;

    public static final double TURRET_TARGETING_K = 0.026;
    public static final double TURRET_TARGETING_REST_POWER = 0.02;

    public static final double SHOOT_ANGLE = 52;
    public static final double g = 9.81;

    public static final double SHOOT_RATIO_1 = 1;


    public static final double SHOOT_RATIO_23 = SHOOT_RATIO_1*1.6;

    @Override
    public void init() {
        turret = createMotor("tu", MOTOR_FORWARD, MOTOR_BRAKE);
        shooter = createMotorWithEncoderRotational("sh", MOTOR_FORWARD, MOTOR_FLOAT, false, 28.0, 1);
        timer.reset();
        timer2.reset();
        limey = hardwareMap.get().get(Limelight3A.class, "shuhulsdumb");
        limey.pipelineSwitch(8);
        limey.start();
        imu = hardwareMap.get().get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT, RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
    }

    public Pose getPoseWithLimey(){
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        limey.updateRobotOrientation(orientation.getYaw());
        LLResult llResult = limey.getLatestResult();
        if (fieldSide == FieldSide.RED){
            limey.pipelineSwitch(8);
        }
        else{
            limey.pipelineSwitch(9);
        }

        if (llResult != null && llResult.isValid()) {
        int desiredTag = 0;
//        if(fieldSide == FieldSide.BLUE){
//            desiredTag = 20;
//        }
//        else{
//            desiredTag = 24;
//        }

//            if (llResult != null && llResult.isValid()) {
//                for (LLResultTypes.FiducialResult fid : llResult.getFiducialResults()) {
//                    if (fid.getFiducialId() != desiredTag){
//                        continue;
//                    }
                    double ty = llResult.getTy();

                    double distance = HEIGHT_DIFFERENCE/Math.tan(Math.toRadians(MOUNT_ANGLE+ty));
                    double angle = llResult.getTx();

                    return new Pose(0, distance, angle);
//                }



//            Pose3D botPose = llResult.getBotpose_MT2();
//            return botPose;

        }else{
            return new Pose(0,0,0);
        }
    }
    public double calculateExitVelocity(double distanceMeters) {
        double theta = Math.toRadians(Turret.SHOOT_ANGLE); // shooter angle in radians
        double h = Turret.HEIGHT_DIFFERENCE / 100.0;       // cm → meters
        double d = distanceMeters;

        double numerator = Turret.g * d * d;
        double denominator = 2 * Math.pow(Math.cos(theta), 2) * (d * Math.tan(theta) - h);

        if (denominator <= 0) {
            return 0; // impossible shot
        }

        return Math.sqrt(numerator / denominator);
    }

    public double velocityToRPM(double velocity) {
        double wheelRadius = 0.1016/2;  // 4-inch wheel → meters
        double rpmEfficiency = 1.23;      // fudge factor for slip, compression, etc.

        return Math.max(2000, Math.min(3000, (2*velocity / (2 * Math.PI * wheelRadius)) * 60 * rpmEfficiency));

    }

    public double getShooterRPMFromLimelight() {
        Pose pose = getPoseWithLimey();           // uses Limelight
        double distanceMeters = pose.y / 100.0;   // cm → meters
        double velocity = calculateExitVelocity(distanceMeters);
        return velocityToRPM(velocity);
    }

    public void turn(double power){
        turret.setPower(power);
    }

    public void shoot(double power){
        shooter.setPower(power);
    }

    public Runnable setShootTarget(double rpm){
        return () -> shooter.setTargetVelocity(rpm);
    }

    public Runnable resetShootRunmode(){
        return () -> {
            shooter.resetRunMode();
            currentPower = shooter.getPower();
            timer.reset();
        };
    }

    public Supplier<Boolean> isNotReady = () -> {
        double shootError = Math.abs(QbitOp.shooterTarget.get() - shooter.getVelocity());
        double turnError = Math.abs(QbitOp.turnError.get());

        if(turnError > 2 || shootError > 80){
            timer2.reset();
        }else return timer2.seconds() < 0.4;

        return true;
    };

    public Double errorReturn(){
        double shootError = Math.abs(QbitOp.shooterTarget.get() - shooter.getVelocity());
//        double turnError = Math.abs(QbitOp.turnError.get());
        return shootError;
    };


}
