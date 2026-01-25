package robotparts.hardware;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import elements.FieldSide;
import static global.General.fieldSide;


import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.ArrayList;
import java.util.function.Supplier;

import geometry.Pose;
import robotparts.RobotPart;
import robotparts.electronics.Motor;
import robotparts.electronics.MotorWithEncoderRotational;
import teleop.opmodes.QbitOp;
import utility.Timer;

public class Turret extends RobotPart {

    public MotorWithEncoderRotational turret;
    public MotorWithEncoderRotational shooter;
    public Timer timer = new Timer();
    public Timer timer2 = new Timer();

    public double currentPower = 0;
    // Added variable to store the previous calculation
    private double previousVelocity = 0;

    public Limelight3A limey;
    public IMU imu;

    public static final double MOUNT_ANGLE = 15;
    public static final double HEIGHT_DIFFERENCE = 38 + 40;
    public static final double LIMEY_LEFT_DISTANCE = 12;

    public static final double TURRET_TARGETING_K = 0.026;
    public static final double TURRET_TARGETING_REST_POWER = 0.08;

    public static final double SHOOT_ANGLE = 52;
    public static final double g = 9.81;

    public static double SHOOT_RATIO_1 = 1.04;
    public static final double maxTurretRotation = 180;
    public static final double minTurretRotation = -20;

    public static double SHOOT_RATIO_23 = SHOOT_RATIO_1*1.42;

    public ArrayList<Double> velocityArray = new ArrayList<>();

    @Override
    public void init() {
        turret = createMotorWithEncoderRotational("tu", MOTOR_FORWARD, MOTOR_BRAKE, false, 537.6, 4);
        shooter = createMotorWithEncoderRotational("sh", MOTOR_FORWARD, MOTOR_FLOAT, false, 28.0, 1);
        timer.reset();
        timer2.reset();
        limey = QhardwareMap.get().get(Limelight3A.class, "shuhulsdumb");
        limey.pipelineSwitch(8);
        limey.start();
        imu = QhardwareMap.get().get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT, RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));

        velocityArray = new ArrayList<>();
        velocityArray.add(0.0);
        velocityArray.add(0.0);
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
            double ty = llResult.getTy();
            double angle = 0;
            double distance = HEIGHT_DIFFERENCE/Math.tan(Math.toRadians(MOUNT_ANGLE+ty));
                angle = llResult.getTx()-5.5;
            if (angle+turret.getPosition()<minTurretRotation){
                angle = 0;
            }
            if (angle+turret.getPosition()>maxTurretRotation){
                angle =0;
            }
            return new Pose(0, distance, angle);

        }else{
            return new Pose(0,0,turret.getPosition());
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

    public double RPMScaler(double RPM){
        double newRPM =2650.0/2800.0*RPM;
        double inMin = 2800;
        double inMax = 3200;
        double outMin = 2650;
        double outMax = 3250;
        return outMin +(RPM - inMin)*(outMax-outMin)/(inMax-inMin);
    }

    public double velocityToRPM(double velocity) {
        double wheelRadius = 0.1016/2;  // 4-inch wheel → meters
        double rpmEfficiency = SHOOT_RATIO_1;      // fudge factor for slip, compression, etc.

        return Math.max(2000, Math.min(3950, (2*velocity / (2 * Math.PI * wheelRadius)) * 60 * rpmEfficiency));
    }

    public double getShooterRPMFromLimelight() {
        Pose pose = getPoseWithLimey();           // uses Limelight
        double distanceMeters = pose.y / 100.0;   // cm → meters

        // 1. Calculate the instantaneous velocity required
        double rawVelocity = calculateExitVelocity(distanceMeters);

        // 2. Perform Moving Average (If previous is 0, initialize it to current to avoid ramp-up lag)
        if (previousVelocity == 0) {
            previousVelocity = rawVelocity;
        }

        // This averages the current calculation with the result of the previous loop
        double smoothedVelocity = (rawVelocity + previousVelocity) / 2.0;

        // 3. Update the history
        previousVelocity = smoothedVelocity;

        // 4. Use smoothedVelocity for the rest of the calculation
        if (RPMScaler(velocityToRPM(smoothedVelocity)) > 3250){
            return velocityToRPM(smoothedVelocity);
        }
        return RPMScaler(velocityToRPM(smoothedVelocity));
    }

    public double getDistance(){
        Pose pose = getPoseWithLimey();           // uses Limelight
        double distanceMeters = pose.y;   //
        return distanceMeters;
    }

    public void turn(double power){
        turret.setPower(power);
    }

    public void shoot(double power){
        shooter.setPower(power);
    }

//    public Runnable setShootTarget(double rpm){
//        return () -> shooter.setTargetVelocity(rpm);
//    }
//
//    public Runnable resetShootRunmode(){
//        return () -> {
//            shooter.resetRunMode();
//            currentPower = shooter.getPower();
//            timer.reset();
//            previousVelocity = 0; // Optional: Reset filter when mode resets
//        };
//    }
//
//    public Runnable setTurretTarget(double angle, double power){
//        return () -> turret.setTarget(angle, power);
//    }
//
//    public Runnable resetTurretRunmode(){
//        return () -> turret.resetRunMode();
//    }

    public Supplier<Boolean> isNotReady = () -> {
        velocityArray.add(shooter.getVelocity());
        int last = velocityArray.size()-1;
        double averageVelocity = (velocityArray.get(last) + velocityArray.get(last-1) + velocityArray.get(last-2))/3.0;
        // TODO CLEAR ARRAYLIST
        double shootError = Math.abs(QbitOp.shooterTarget.get() - averageVelocity);
        double turnError = Math.abs(QbitOp.turnError.get());

        if(turnError > 1  || shootError > 120){
            timer2.reset();
        }else return timer2.seconds() < 0.5;

        return true;
    };

    public Double errorReturn(){
        double shootError = Math.abs(QbitOp.shooterTarget.get() - shooter.getVelocity());
        return shootError;
    };
}