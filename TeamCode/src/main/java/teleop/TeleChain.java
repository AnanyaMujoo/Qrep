package teleop;

import static chains.StageBuilder.stage;
import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.shooter;

import chains.Chain;
import chains.ChainMaker;

public interface TeleChain {

    ChainMaker shootFar = () -> new Chain(
            //TODO shootFar
            stage(drive, drive.moveRunnable(0.6, 0, 0), 0.2),
            stage(shooter, shooter.intakeRunnable(-0.7), 1),
            stage(drive, drive.moveRunnable(-0.7, 0,0),3),
            stage(drive, drive.moveRunnable(0, 0,0.7),1),

            stage(shooter, shooter.shootRunnable(0.8),2),
            stage(shooter, shooter.shootAndIntakeRunnable(0.8, 0.8),1)
            );


    ChainMaker shootClose = () -> new Chain(
            //TODO shootClose
            stage(drive, drive.moveRunnable(0.1, 0.2, 0.3), 1),
            //stage 1 from identified position move to correvt angle  with drive move runnable
            stage(shooter, shooter.shootRunnable(1))
            //stage 2 shoot at correct power for angle+position
    );

    //TODO make a chainmaker for shooting manually from not identified positions
    ChainMaker shootManual = () -> new Chain(
            stage(shooter, shooter.shootRunnable(1))
    );
//    ChainMaker Test2 = () -> new Chain(
//            stage(drive, drive.moveRunnable(0.1, 0.2, 0.3), 1),
//            stage(encoderTest.setTargetRunnable(10, 0.5), 3),
//            stage(servoTest.servoStartRunnable),
//            stage(encoderTest.setTargetRunnable(20, 0.5), encoderTest.isAtTargetSupplier),
//            stage(servoTest.servoEndRunnable, 3),
//            stage(encoderTest.setTargetRunnable(0, 0.5)),
//            stage(servoTest.servoStartRunnable),
//            stage(3)
//    );

//    ChainMaker Intake = () -> new Chain(
//            stage(intake, intake::lock),
//            stage(intake, () -> QbitOp.isTurretTargeting.set(true)),
//            stage(intake, () -> intake.intakeAndFeed(1), intake.isNotDetected)
////            stage(intake, () -> intake.feed(-1), 0.05),
////            stage(intake, intake::unlock)
////            stage(turret, () -> turret.shoot(0.75), 1)
//
//
////            stage(intake, () -> {intake.intake(1.0); intake.feed(0.5); }, () -> intake.getColorSensorBottomDistance() > 4.1),
////            stage(intake, () -> {intake.feeder.softResetEncoder(); intake.feed(1.0); }, () -> intake.feeder.getPosition() < 600),
////            stage(intake, intake.setFeedTargetRelative(500, 1.0), intake.isFeedAtTargetSupplier),
////            stage(intake, () -> {intake.intake(1.0); }, () -> intake.getColorSensorBottomDistance() > 4.1),
////            stage(intake, () -> {intake.feeder.softResetEncoder(); intake.feed(1.0); }, () -> intake.feeder.getPosition() < 80),
////            stage(intake, intake.setFeedTargetRelative(200, 1.0), intake.isFeedAtTargetSupplier),
////            stage(intake, () -> intake.intake(1.0), 0.5),
////            stage(intake, () -> intake.intake(0.5), 2.0, intake.resetFeedRunMode())
//    );
//
//    ChainMaker Shoot = () -> new Chain(
//            stage(intake, () -> intake.feed(-1), 0.05),
//            stage(intake, intake::unlock, 0.1),
//            stage(intake, () -> {}, turret.isNotReady),
//            stage(intake, () -> QbitOp.isTurret23Mode.set(true)),
//            stage(intake, () -> intake.intakeAndFeed(1), 1),
//            stage(intake, () -> QbitOp.isTurretTargeting.set(false)),
//            stage(intake, () -> QbitOp.isTurret23Mode.set(false))
//
//
//
//
////            stage(turret, turret::resetShootRunmode),
////            stage(intake, turret,  () -> {
////                intake.intakeAndFeed(1);
////                turret.shoot(1);
////                if(turret.timer.seconds() < 0.1) {
////                    turret.shoot(turret.currentPower);
////                }else{
////                    turret.shoot(1);
////                }
////            }, 1)
//
////            stage(turret, () -> turret.shoot(0.75), 2),
////            stage(turret, intake,  () -> {turret.shoot(0.9); intake.feed(1.0); intake.intake(1.0); }, 2)
////            stage(turret, () -> turret.shoot(0.8), 2),
////            stage(turret, intake,  () -> {turret.shoot(0.9); intake.feed(1.0); }, 0.2),
////            stage(turret, () -> turret.shoot(0.8), 2),
////            stage(turret, intake,  () -> {turret.shoot(0.9); intake.feed(1.0); }, 0.2)
//    );

}