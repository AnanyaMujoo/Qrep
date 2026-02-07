package pedroPathing;

import static chains.StageBuilder.stage;
import static robotparts.RobotConfig.intake;
import static robotparts.RobotConfig.turret;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import auto.Auto;
import chains.Chain;
import chains.ChainMaker;

@Autonomous(name = "QRedFarShooting", group = "Autonomous")
@Configurable
public class QRedFar extends Auto {



    public Follower follower;
    public Follower followerBoosted;
    public ArrayList<PathChain> paths = new ArrayList<>();


    public Pose lastPose = new Pose(0,0);
    public double lastHeading = 0;


    public int currentIndex = 0;


    public ArrayList<ChainMaker> chains = new ArrayList<>();

    public ArrayList<PathType> pathTypes = new ArrayList<>();


    public AtomicBoolean isChainRunning = new AtomicBoolean(false);


    public enum PathType {
            PATH,
            PATH_BOOSTED,
            CHAIN,
            CONCURRENT_CHAIN
    }

    public static final ChainMaker EMPTY_CHAIN = () -> new Chain();


    public static AtomicReference<Double> shooterTarget = new AtomicReference<>(0.0);
    public static AtomicReference<Double> oldTarget = new AtomicReference<>(0.0);





    public void addLine(double x, double y, double h1, double h2){
        paths.add(follower.pathBuilder()
                .addPath(new BezierLine(lastPose, new Pose(x, y)))
                .setLinearHeadingInterpolation(Math.toRadians(h1), Math.toRadians(h2))
                .build());
        lastPose = new Pose(x,y);
        lastHeading = Math.toRadians(
                h2);

        chains.add(EMPTY_CHAIN);
        pathTypes.add(PathType.PATH);
    }

    public void addLineBoost(double x, double y, double h1, double h2){
        paths.add(followerBoosted.pathBuilder()
                .addPath(new BezierLine(lastPose, new Pose(x, y)))
                .setLinearHeadingInterpolation(Math.toRadians(h1), Math.toRadians(h2))
                .build());
        lastPose = new Pose(x,y);
        lastHeading = Math.toRadians(
                h2);

        chains.add(EMPTY_CHAIN);
        pathTypes.add(PathType.PATH_BOOSTED);
    }


    public void addChain(ChainMaker chain){
        ChainMaker newChain = () -> new Chain(chain.get().getStages()).add(stage(() -> isChainRunning.set(false)));
        chains.add(newChain);
        paths.add(new PathChain());
        pathTypes.add(PathType.CHAIN);
    }

    public void addConcurrentChain(ChainMaker chain){
        chains.add(chain);
        paths.add(new PathChain());
        pathTypes.add(PathType.CONCURRENT_CHAIN);
    }


    public static final double SHOOT_1 = 3540;
    public static final double SHOOT_23 = 1180+2500;

    ChainMaker SpinUpQ = () -> new Chain(
            stage(() -> shooterTarget.set(SHOOT_1))
    );
    ChainMaker ShootFirstQ = () -> new Chain(
            stage(() -> shooterTarget.set(SHOOT_23)),
            stage(intake, () -> intake.intakeAndFeed(1), 1)
    );
    ChainMaker IntakeQ1 = () -> new Chain(
            stage(intake, intake::lock, 0.1),
            stage(intake, () -> intake.intakeAndFeed(0.7), 2)
    );
    ChainMaker ShootSecondQ = () -> new Chain(
            stage(intake, intake.setFeedTargetRelative(-240, 0.4), () -> !intake.feeder.isMotorAtTarget()),
            stage(intake, intake::unlock, 0.2),
            stage(intake, intake.resetFeedRunMode()),
            stage(() -> shooterTarget.set(SHOOT_23)),
            stage(intake, () -> intake.intakeAndFeed(1), 1)
    );

    ChainMaker IntakeQ = () -> new Chain(
            stage(intake, intake::lock),
            stage(intake, () -> intake.intakeAndFeed(0.7), 2)
    );

    ChainMaker IntakeAndSpinUpQ = () -> new Chain(
            stage(intake, intake::lock),
            stage(intake, () -> intake.intakeAndFeed(1 ), 2),
            stage(() -> shooterTarget.set(SHOOT_1))
    );
ChainMaker Lock= () -> new Chain(
        stage(intake, intake::lock)
);
    ChainMaker Wait = () -> new Chain(
            stage(() -> {}, 0.25)
    );

    public static FollowerConstants followerConstants2 = new FollowerConstants()
            .mass(12.55)
            .forwardZeroPowerAcceleration(-43.8014)
            .lateralZeroPowerAcceleration(-74.98348806)
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.001, 0.0,0.00, 0.00, 0.2))
            .translationalPIDFCoefficients(new PIDFCoefficients(0.0006, 0.0,0.00, 0.13));






    @Override
    public void initAuto() {
        follower = Constants.createFollower(hardwareMap);
        followerBoosted = new FollowerBuilder(followerConstants2, hardwareMap)
                .pinpointLocalizer(Constants.localizerConstants)
                .pathConstraints(Constants.pathConstraints)
                .mecanumDrivetrain(Constants.driveConstants)
                .build();
        follower.setStartingPose(new Pose(0, 0, Math.toRadians(0)));
        followerBoosted.setStartingPose(new Pose(0, 0, Math.toRadians(0)));

        paths = new ArrayList<>();
        lastHeading = Math.toRadians(0);
        turret.shooter.setPIDF(25, 0, 0.00000, 13.5);
        intake.unlock();
        isChainRunning.set(false);
        turret.turret.setTarget(-20, 0.5);
        addConcurrentChain(SpinUpQ);
        addLine(15,0,0,0);
        addChain(ShootFirstQ);
        addLine(15,20,0,0);
        turret.turret.setTarget(0, 0.5);


    }


    public void update(){
        double st = shooterTarget.get();
        if(st != oldTarget.get()){
            if(st != 0) {
                turret.shooter.setTargetVelocity(st);
            }else{
                turret.shooter.resetRunMode();
                turret.shoot(0.0);
            }
            oldTarget.set(st);
        }
    }

    @Override
    public void runAuto() {
        while (opModeIsActive()) {

            PathType currentPathType = pathTypes.get(currentIndex);

            if(currentPathType.equals(PathType.PATH) || currentPathType.equals(PathType.PATH_BOOSTED)) {
                PathChain current = paths.get(currentIndex);
                if(currentPathType.equals(PathType.PATH)) {
                    follower.followPath(current);
                    while (opModeIsActive() && follower.isBusy()) {
                        follower.update();
                        update();
                    }
                }else{
                    followerBoosted.followPath(current);
                    while (opModeIsActive() && followerBoosted.isBusy()) {
                        followerBoosted.update();
                        update();
                    }
                }
            } else if (currentPathType.equals(PathType.CHAIN)) {
                ChainMaker currentChain = chains.get(currentIndex);
                currentChain.run();
                isChainRunning.set(true);
                while (opModeIsActive() && isChainRunning.get()){
                    update();
                }

            } else if(currentPathType.equals(PathType.CONCURRENT_CHAIN)){
                ChainMaker currentChain = chains.get(currentIndex);
                currentChain.run();
                update();
            }
            currentIndex++;
        }

    }
}