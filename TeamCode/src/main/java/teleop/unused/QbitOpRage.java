package teleop.unused;

import static global.General.fieldSide;
import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.turret;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.util.concurrent.atomic.AtomicBoolean;

import elements.FieldSide;
import geometry.Pose;
import pedroPathing.Constants;
import teleop.Tele;
import teleop.teleutil.Button;

@TeleOp
public class QbitOpRage extends Tele {


    public double[] distances = new double[]{   186, 241, 310, 366, 426, 499, 560, 620,  745, 760};
    public double[] realDistances = new double[]{36,  48,  60,  72,  84,  96, 108,  120, 132, 144};

    public AtomicBoolean autoMovement = new AtomicBoolean(false);
    public int autoIndex = 0;


    public Follower follower;
    double startHeading = 0;

    @Override
    public void initTele() {

        turret.turret.hardResetEncoder();
        drive.pinpoint.resetPosAndIMU();

        autoMovement.set(false);
        autoIndex = 0;

        FollowerConstants followerConstants2 = new FollowerConstants()
                .mass(12.55)
                .forwardZeroPowerAcceleration(-43.8014)
                .lateralZeroPowerAcceleration(-74.98348806)
                .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.02, 0.0,0.00, 0.00, 0.0))
                .translationalPIDFCoefficients(new PIDFCoefficients(0.005, 0.0,0.00, 0.04));
        follower = new FollowerBuilder(followerConstants2, hardwareMap)
                .pinpointLocalizer(Constants.localizerConstants)
                .pathConstraints(Constants.pathConstraints)
                .mecanumDrivetrain(Constants.driveConstants)
                .build();
        follower.setStartingPose(new com.pedropathing.geometry.Pose(0, 0, Math.toRadians(0)));

//        drive.pinpoint.setHeading(225, AngleUnit.DEGREES);

        gpA.onClick(Button.LEFT_TRIGGER, () -> drive.pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0,0, AngleUnit.DEGREES, 0)));
        gpA.onClick(Button.RIGHT_TRIGGER, () -> autoMovement.set(true));

    }


    @Override
    public void startTele() {
    }

    @Override
    public void loopTele() {

        if(!autoMovement.get()){
            drive.move(0.7*gpA.ry, 0.7*gpA.rx, 0.6*gpA.lx);
            drive.updateOdometry();
        }else{
            if(autoIndex == 0){
                Pose2D currentPosition = drive.getOdoPose();
                PathChain pathChain;
                startHeading = currentPosition.getHeading(AngleUnit.RADIANS);
                double attackAngle;
                if(currentPosition.getX(DistanceUnit.INCH) > 0) {
                    attackAngle = Math.atan2(currentPosition.getY(DistanceUnit.INCH), currentPosition.getX(DistanceUnit.INCH));
                }else{
                    attackAngle = Math.atan2(-currentPosition.getY(DistanceUnit.INCH), -currentPosition.getX(DistanceUnit.INCH));
                }
                double distance = Math.sqrt(currentPosition.getX(DistanceUnit.INCH)*currentPosition.getX(DistanceUnit.INCH) + currentPosition.getY(DistanceUnit.INCH)*currentPosition.getY(DistanceUnit.INCH));

//                double finalAngle;
//                if(distance < 20){
//                    finalAngle = 0.0;
//                }else{
//                    finalAngle = attackAngle;
//                }
                double factor = Math.exp(-distance/20);
                double finalAngle = (attackAngle*(1-factor));
                turret.turret.softResetEncoder();
                turret.turret.setTarget(Math.toDegrees(finalAngle), 0.15);
                pathChain = follower.pathBuilder()
                        .addPath(new BezierLine(new com.pedropathing.geometry.Pose(currentPosition.getX(DistanceUnit.INCH), currentPosition.getY(DistanceUnit.INCH), Math.toRadians(0)), new com.pedropathing.geometry.Pose(0, 0, Math.toRadians(0))))
                        .setLinearHeadingInterpolation(currentPosition.getHeading(AngleUnit.RADIANS), finalAngle)
                        .build();
                follower.followPath(pathChain);
                autoIndex++;
            } else if (autoIndex == 1) {
                follower.update();
                if(!follower.isBusy()){
                    autoIndex++;
                }
            }else if(autoIndex == 2){
                if(turret.turret.isMotorAtTarget()) {
                    autoMovement.set(false);
                    turret.turret.resetRunMode();
                    turret.turret.setPower(0.0);
                    autoIndex = 0;
                }
            }
        }

//        drive.updateOdometry();
//        display("Distance", turret.getDistance());

        Pose pose = turret.getPoseInches();
        display("Inches", pose.getY());
        display("Angle", pose.getAngle());
        display("TurretAngle", turret.getTurretAngleFromStart());
        display("Odo X | Y | H", String.format("%.1f | %.1f | %.1f", drive.getX(), drive.getY(), drive.getHeading()));
        display("startHeading", startHeading);


    }

    @Override
    public void stopTele() {
        turret.limey.stop();
    }
//    @TeleOp(name = "BlueTeleOpRage", group = "TeleOp")
//    public static class BlueTeleOp2 extends QbitOpRage {{fieldSide = FieldSide.BLUE; }}
//
//    @TeleOp(name = "RedTeleOpRage", group = "TeleOp")
//    public static class RedTeleOp2 extends QbitOpRage {{fieldSide = FieldSide.RED; }}
}
