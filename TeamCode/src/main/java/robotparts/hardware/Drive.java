package robotparts.hardware;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import geometry.Pose;
import geometry.Vector;
import robotparts.hardware.templates.DriveTemplate;

public class Drive extends DriveTemplate {
    public GoBildaPinpointDriver pinpoint;

    @Override
    public void init() {
        super.init();
        pinpoint = QhardwareMap.get().get(GoBildaPinpointDriver.class, "od");
        /*
         *  Set the odometry pod positions relative to the point that you want the position to be measured from.
         *
         *  The X pod offset refers to how far sideways from the tracking point the X (forward) odometry pod is.
         *  Left of the center is a positive number, right of center is a negative number.
         *
         *  The Y pod offset refers to how far forwards from the tracking point the Y (strafe) odometry pod is.
         *  Forward of center is a positive number, backwards is a negative number.
         */
        pinpoint.setOffsets(-159, -140, DistanceUnit.MM); //these are tuned for 3110-0002-0001 Product Insight #1
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        /*
         * Set the direction that each of the two odometry pods count. The X (forward) pod should
         * increase when you move the robot forward. And the Y (strafe) pod should increase when
         * you move the robot to the left.
         */
        //TODO check-+
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        pinpoint.resetPosAndIMU();

        pinpoint.setPosition(new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0));
    }

    public static final Vector fieldCenterToTarget = new Vector(81, 0).rotate(Math.toRadians(221));


//    public void setHeading


    public void updateOdometryFromLimey(Pose fromLimey){
        Vector targetToLimey = new Vector(fromLimey.getY(), 2).rotate(Math.toRadians(45));
        Vector fieldCenterToLimey = fieldCenterToTarget.add(targetToLimey);
        Pose2D robotPose = new Pose2D(DistanceUnit.INCH, fieldCenterToLimey.getX(), fieldCenterToLimey.getY(), AngleUnit.DEGREES, 225);
    }

//    public Pose toFieldCentric(Pose fromLimey, double turretAngle){
//        double turretAngleProper = -turretAngle;
//        Vector targetToLimey = new Vector(fromLimey.getY(), 0).rotate(Math.toRadians(45+fromLimey.getAngle()));
//
//
//
//
//    }





    public void updateOdometry() {
        pinpoint.update();
    }

    public void setPosition(Pose pose){
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, pose.getX(), pose.getY(), AngleUnit.DEGREES, pose.getAngle()));
    }

    public Pose2D getOdoPose() {
        return pinpoint.getPosition();
    }

    public double getX() {
        return pinpoint.getPosition().getX(DistanceUnit.INCH);
    }

    public double getY() {
        return pinpoint.getPosition().getY(DistanceUnit.INCH);
    }


    public double getHeading() {
        return pinpoint.getPosition().getHeading(AngleUnit.DEGREES);
    }
}

