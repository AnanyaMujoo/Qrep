package pedroPathing;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;
import static chains.StageBuilder.stage;
import static pedroPathing.QAuto.PathState.DONE;
import static pedroPathing.QAuto.PathState.SHOOT1;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;

import global.Initializer;
import teleop.TeleChain;
import chains.Stage;

import static robotparts.RobotConfig.intake;
import static robotparts.RobotConfig.turret;
import static teleop.TeleChain.Shoot;
import static teleop.TeleChain.ShootAuto;


@Autonomous(name = "Pedro Pathing Autonomous", group = "Autonomous")
@Configurable // Panels
public class QAuto extends OpMode implements Initializer {
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private PathState pathState; // Current autonomous path state (state machine)
    private Paths paths; // Paths defined in the Paths class
    private Timer pathTimer, opModeTimer;
    @Override
    public void init() {
        _init(this);
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        pathState = PathState.DRIVE_SHOOT1;
        follower = Constants.createFollower(hardwareMap);
//        follower.setStartingPose(new Pose(25.479, 127.624, Math.toRadians(225)));
        follower.setStartingPose(new Pose(-56.5, -46.5, Math.toRadians(45)));
        Pose endingPose = new Pose(-56.5, -46.5, Math.toRadians(45));

//
//        follower.setStartingPose(new Pose(0, 0, Math.toRadians(0)));
        paths = new Paths(follower); // Build paths

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
        pathTimer = new Timer();
        opModeTimer = new Timer();
        opModeTimer.resetTimer();

    }
    public enum PathState {
        DRIVE_SHOOT1,
        SHOOT1,
        DONE,
        DRIVE_INTAKE1,
        DRIVE_INTAKE2,
        DRIVE_INTAKE3,
        INTAKE

    }
    public void setPathState(PathState runState){
        pathState = runState;
        pathTimer.resetTimer();
    }
    @Override
    public void loop() {

        follower.update(); // Update Pedro Pathing
        autonomousPathUpdate(); // Update autonomous state machine

        // Log values to Panels and Driver Station
        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());
        panelsTelemetry.update(telemetry);
        _loop();

    }

    @Override
    public void stop() {
        _stop();
    }

    public static class Paths {
        public PathChain Path1, Path2, Path3;


        public Paths(Follower follower) {
            Path1 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(-56.5, -46.5),

                                    new Pose(-24, -14)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(45))
                    .setGlobalDeceleration(1)
                    .build();
//            Path1 = follower.pathBuilder().addPath(
//                            new BezierLine(
//                                    new Pose(25.479, 127.624),
//
//                                    new Pose(59.482, 84.137)
//                            )
//                    ).setLinearHeadingInterpolation(Math.toRadians(225), Math.toRadians(90))
//
//                    .build();
//            Path2 = follower.pathBuilder().addPath(
//                            new BezierLine(
//                                    new Pose(10, 0),
//
//                                    new Pose(10, 10)
//                            )
//                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
//
//                    .build();
//
//            Path3 = follower.pathBuilder().addPath(
//                            new BezierLine(
//                                    new Pose(19.050, 83.983),
//
//                                    new Pose(59.478, 84.368)
//                            )
//                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(225))
//
//                    .build();
        }
    }


    public PathState autonomousPathUpdate() {
        switch (pathState) {

            case DRIVE_SHOOT1:
                // Start Path 1
                follower.followPath(paths.Path1, true);
                setPathState(SHOOT1);

                break;

            case SHOOT1:
                if(!follower.isBusy()){
                    //add shoot code
                    ShootAuto.run();
                    telemetry.addLine("Done Path 1");
//
                }
                setPathState(DONE);
                break;

//            case 1:
//                // Wait for Path 1 to finish
//                if (!follower.isBusy()) {
//                    follower.followPath(paths.Path2, true);
//                    pathState = 2;
//                }
//                break;

//            case 2:
//                // Wait for Path 2 to finish
//                if (!follower.isBusy()) {
//                    follower.followPath(paths.Path3, true);
//                    pathState = 3;
//                }
//                break;
//
//            case 3:
//                // Wait for Path 3 to finish
//                if (!follower.isBusy()) {
//                    pathState = 4; // Done
//                }
//                break;
//
//            case 4:
//                // Autonomous complete — do nothing
//                break;
        }

        return pathState;
    }
}