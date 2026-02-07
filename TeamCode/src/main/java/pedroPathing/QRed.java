package pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;

import auto.Auto;
import chains.Chain;
import chains.ChainMaker;

import static chains.StageBuilder.stage;
import static robotparts.RobotConfig.intake;
import static robotparts.RobotConfig.turret;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Autonomous(name = "QRedAuto", group = "Autonomous")
@Configurable
public class QRed extends Auto {



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


    public static final double SHOOT_1 = 2300;
    public static final double SHOOT_23 = 2800;

    ChainMaker SpinUpQ = () -> new Chain(
            stage(() -> shooterTarget.set(SHOOT_1))
    );
    ChainMaker ShootFirstQ = () -> new Chain(
            stage(() -> shooterTarget.set(SHOOT_23)),
            stage(intake, () -> intake.intakeAndFeed(1), 0.9)
    );

    ChainMaker ShootSecondQ = () -> new Chain(
            stage(intake, intake.setFeedTargetRelative(-360, 0.6),0.6),
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
            stage(intake, () -> intake.intakeAndFeed(0.7), 2),
            stage(() -> shooterTarget.set(SHOOT_1))
    );

    ChainMaker Wait = () -> new Chain(
            stage(() -> {}, 0.125)
    );
    ChainMaker WaitForShoot = () -> new Chain(
            stage(() -> {}, 0.25)
    );
    public static FollowerConstants followerConstants2 = new FollowerConstants()
            .mass(12.55)
            .forwardZeroPowerAcceleration(-43.8014)
            .lateralZeroPowerAcceleration(-74.98348806)
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.001, 0.0,0.00, 0.00, 0.2))
            .translationalPIDFCoefficients(new PIDFCoefficients(0.001, 0.0,0.00, 0.2));






    @Override
    public void initAuto() {
        follower = Constants.createFollower(hardwareMap);
        followerBoosted = new FollowerBuilder(followerConstants2, hardwareMap)
                .pinpointLocalizer(Constants.localizerConstants)
                .pathConstraints(Constants.pathConstraints)
                .mecanumDrivetrain(Constants.driveConstants)
                .build();


        follower.setStartingPose(new Pose(-56.5, 46.5, Math.toRadians(135)));
        followerBoosted.setStartingPose(new Pose(-56.5, 46.5, Math.toRadians(135)));
        paths = new ArrayList<>();
        lastHeading = Math.toRadians(135);
        lastPose = new Pose(-56.5, 46.5, Math.toRadians(135));
        currentIndex = 0;
        pathTypes = new ArrayList<>();
        chains = new ArrayList<>();
        turret.shooter.setPIDF(25, 0, 0.00000, 13.5);
        intake.unlock();
        isChainRunning.set(false);
        addConcurrentChain(SpinUpQ);
        addLine(-24,24,135,135);
        addChain(WaitForShoot);
        addChain(WaitForShoot);
        addChain(ShootFirstQ);
        addLine(-14, 24, 90,90);
        addConcurrentChain(IntakeQ);
        addLineBoost(-14, 56, 90,90);
        addLine(-7, 46, 0,0);
        addConcurrentChain(SpinUpQ);
        addLine(-7, 58, 0,0);
        addChain(Wait);
        addLine(-24,24,135,135);
        addChain(ShootSecondQ);
        addLine(11,24,135,90);
        addConcurrentChain(IntakeAndSpinUpQ);
        addLineBoost(11,57,90,90);
        addLine(-24,24,90,135);
        addChain(ShootSecondQ);
        addLine(33,24,90,90);
        addConcurrentChain(IntakeAndSpinUpQ);
        addLineBoost(33,60,90,90);
        addLine(-40,14,90,120);
        addChain(ShootSecondQ);
        addLine(-40,14,90,135);


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