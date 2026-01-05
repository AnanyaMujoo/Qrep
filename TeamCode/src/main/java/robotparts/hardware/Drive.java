package robotparts.hardware;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import robotparts.hardware.templates.DriveTemplate;

public class Drive extends DriveTemplate {
    GoBildaPinpointDriver pinpoint;

    @Override
    public void init() {
        super.init();
        pinpoint = hardwareMap.get().get(GoBildaPinpointDriver.class, "od");
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
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED,
                GoBildaPinpointDriver.EncoderDirection.REVERSED);

        pinpoint.resetPosAndIMU();

        pinpoint.setPosition(new Pose2D(DistanceUnit.MM, 0, 0, AngleUnit.DEGREES, 0));
    }


    public void updateOdometry() {
        pinpoint.update();
    }

    public Pose2D getOdoPose() {
        return pinpoint.getPosition();
    }

    public double getX() {
        return pinpoint.getPosition().getX(DistanceUnit.CM);
    }

    public double getY() {
        return pinpoint.getPosition().getY(DistanceUnit.CM);
    }


    public double getHeading() {
        return pinpoint.getPosition().getHeading(AngleUnit.DEGREES);
    }
}

