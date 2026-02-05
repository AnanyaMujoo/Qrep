package teleop;

import static chains.StageBuilder.stage;
import static robotparts.RobotConfig.intake;
import static robotparts.RobotConfig.turret;

import chains.Chain;
import chains.ChainMaker;
import teleop.opmodes.QbitOpCopy;

public interface TeleChainCopy {

    ChainMaker IntakeAuto = () -> new Chain(
            stage(intake, intake::lock),
            stage(intake, () -> intake.intakeAndFeed(1), intake.isNotDetected)
    );

    ChainMaker Shoot = () -> new Chain(
            // 1. Prepare
            stage(intake, () -> QbitOpCopy.isTurretTargeting.set(true)),
            stage(intake, () -> QbitOpCopy.isTurret23Mode.set(false)),
            stage(intake, intake.setFeedTargetRelative(-360, 1), () -> !intake.feeder.isMotorAtTarget()),
            stage(intake, intake::unlock, 0.2),
            stage(intake, intake.resetFeedRunMode()),

            // 2. Wait for Indexer
            stage(intake, () -> {}, intake.isReady),

            // 3. Wait for Turret (Using QbitOpCopy's variable!)
            stage(intake, () -> {}, () -> !QbitOpCopy.readyToShoot.get()),

            // 4. Fire!
            stage(intake, () -> {
                QbitOpCopy.isTurret23Mode.set(true);
            }),
            stage(intake, () -> intake.intakeAndFeed(1), 2.0),

            // 5. Reset
            stage(intake, () -> QbitOpCopy.isTurretTargeting.set(false)),
            stage(intake, () -> QbitOpCopy.isTurret23Mode.set(false))
    );

    ChainMaker ShootFar = () -> new Chain(
            stage(intake, () -> QbitOpCopy.isTurret23Mode.set(false)),
            stage(intake, intake.setFeedTargetRelative(-360, 1), () -> !intake.feeder.isMotorAtTarget()),
            stage(intake, intake::unlock, 0.2),
            stage(intake, intake.resetFeedRunMode()),
            stage(intake, () -> {}, intake.isReady),

            // Wait for QbitOpCopy.readyToShoot
            stage(intake, () -> {}, () -> !QbitOpCopy.readyToShoot.get()),

            stage(intake, () -> intake.intakeAndFeed(0.5), 0.5),
            stage(intake, () -> QbitOpCopy.isTurret23Mode.set(true)),
            stage(intake, () -> intake.intakeAndFeed(0.5), 2.0),
            stage(intake, () -> QbitOpCopy.isTurretTargeting.set(false)),
            stage(intake, () -> QbitOpCopy.isTurret23Mode.set(false))
    );

    ChainMaker JustIntake = () -> new Chain(
            stage(intake, intake::unlock, 0.1),
            stage(intake, () -> intake.intakeAndFeed(1), 1)
    );
}