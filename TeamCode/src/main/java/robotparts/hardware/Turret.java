package robotparts.hardware;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.concurrent.atomic.AtomicReference;

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
    public double currentPower = 0;

    public Limelight3A limey;
    public IMU imu;

    public static final double MOUNT_ANGLE = 15;
    public static final double HEIGHT_DIFFERENCE = 38 + 40;
    public static final double LIMEY_LEFT_DISTANCE = 12;

    public static final double TURRET_TARGETING_K = 0.026;
    public static final double TURRET_TARGETING_REST_POWER = 0.02;

    public static final double SHOOT_ANGLE = 52;
    public static final double g = 9.83;

    public static final double SHOOT_RATIO_1 = 1.5;


    public static final double SHOOT_RATIO_23 = SHOOT_RATIO_1*1.1;

    @Override
    public void init() {
        turret = createMotor("tu", MOTOR_FORWARD, MOTOR_BRAKE);
        shooter = createMotorWithEncoderRotational("sh", MOTOR_FORWARD, MOTOR_FLOAT, false, 28.0, 1);
        timer.reset();
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
        if (llResult != null && llResult.isValid()) {

            double ty = llResult.getTy();

            double distance = HEIGHT_DIFFERENCE/Math.tan(Math.toRadians(MOUNT_ANGLE+ty));
            double angle = llResult.getTx();

            return new Pose(0, distance, angle);


//            Pose3D botPose = llResult.getBotpose_MT2();
//            return botPose;

        }else{
            return new Pose(0,0,0);
        }
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


    public boolean isReady(){
        double shootError = Math.abs(QbitOp.shooterTarget.get() - shooter.getVelocity());
        double turnError = Math.abs(QbitOp.turnError.get());

        return turnError < 1 && shootError < 100;
    }
}
