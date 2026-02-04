package teleop;

import static chains.StageBuilder.stage;
import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.intake;
import static robotparts.RobotConfig.turret;

import android.content.QuickViewConstants;

import java.util.function.Supplier;

import chains.Chain;
import chains.ChainMaker;
import chains.Stage;
import teleop.opmodes.QbitOp;
import teleop.opmodes.QbitOpCopy;

public interface TeleChain {
    // --- REVAMPED INTAKE: Starts targeting immediately and runs intake ---
    ChainMaker RevampedIntake = () -> new Chain(
            stage(intake, intake::lock),
            stage(intake, () -> QbitOpCopy.isTurretTargeting.set(true)),
            // This stage will run CONTINUOUSLY until the condition is met
            stage(intake, () -> {
                intake.intake(1.0);
                intake.feed(1.0);
            }, () -> intake.getDetectDistance() < 4.1),
            // Stop once ball is found
            stage(intake, () -> {
                intake.intake(0);
                intake.feed(0);
            })
    );
    // --- REVAMPED SHOOT: Waits for aim, then triggers the succession brain ---
    ChainMaker RevampedShoot = () -> new Chain(
            stage(intake, () -> intake.feed(-1), 0.05), // Anti-jam flick
            stage(intake, intake::unlock, 0.1),
            // WAIT for alignment and RPM stability
            stage(intake, () -> {}, () -> {
                double error = QbitOpCopy.turnError.get();
                double velError = Math.abs(turret.shooter.getVelocity() - QbitOpCopy.shooterTarget.get());
                return Math.abs(error) < 1.5 && velError < 150;
            }),
            // Trigger the intake - the loopTele succession logic handles the rest
            stage(intake, () -> intake.intakeAndFeed(1), 1.2),
            stage(intake, () -> QbitOpCopy.isTurretTargeting.set(false))
    );

    // --- REVAMPED BLITZ: Pure high-speed firing ---
    ChainMaker RevampedBlitz = () -> new Chain(
            stage(intake, intake::unlock),
            stage(intake, () -> QbitOpCopy.isTurretTargeting.set(true)),
            stage(intake, () -> intake.intakeAndFeed(1), 1.5), // Brute force feed for 1.5s
            stage(intake, () -> QbitOpCopy.isTurretTargeting.set(false)),
            stage(intake, intake::stop)
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

    ChainMaker Intake = () -> new Chain(
            stage(intake, intake::lock),
            stage(intake, () -> QbitOp.isTurretTargeting.set(true)),
            stage(intake, () -> intake.intakeAndFeed(1), intake.isNotDetected)
//            stage(intake, () -> intake.feed(-1), 0.05),
//            stage(intake, intake::unlock)
//            stage(turret, () -> turret.shoot(0.75), 1)


//            stage(intake, () -> {intake.intake(1.0); intake.feed(0.5); }, () -> intake.getColorSensorBottomDistance() > 4.1),
//            stage(intake, () -> {intake.feeder.softResetEncoder(); intake.feed(1.0); }, () -> intake.feeder.getPosition() < 600),
//            stage(intake, intake.setFeedTargetRelative(500, 1.0), intake.isFeedAtTargetSupplier),
//            stage(intake, () -> {intake.intake(1.0); }, () -> intake.getColorSensorBottomDistance() > 4.1),
//            stage(intake, () -> {intake.feeder.softResetEncoder(); intake.feed(1.0); }, () -> intake.feeder.getPosition() < 80),
//            stage(intake, intake.setFeedTargetRelative(200, 1.0), intake.isFeedAtTargetSupplier),
//            stage(intake, () -> intake.intake(1.0), 0.5),
//            stage(intake, () -> intake.intake(0.5), 2.0, intake.resetFeedRunMode())
    );

    ChainMaker Shoot = () -> new Chain(
            stage(intake, () -> QbitOp.isTurret23Mode.set(false)),
            stage(intake, intake.setFeedTargetRelative(-360, 1), () -> !intake.feeder.isMotorAtTarget()),
//            stage(intake, () -> intake.feed(-0.5), 0.3),
            stage(intake, intake::unlock, 0.2),
            stage(intake, intake.resetFeedRunMode()),
            stage(intake, () -> {}, intake.isReady),
            stage(intake, () -> {}, turret.isNotReady),
            stage(intake, () -> {
                QbitOp.isTurret23Mode.set(true);
            }),
            stage(intake, () -> intake.intakeAndFeed(1), 2.0),
            stage(intake, () -> QbitOp.isTurretTargeting.set(false)),
            stage(intake, () -> QbitOp.isTurret23Mode.set(false))






//p,10,i,3,d,0,f,0



//            stage(turret, turret::resetShootRunmode),
//            stage(intake, turret,  () -> {
//                intake.intakeAndFeed(1);
//                turret.shoot(1);
//                if(turret.timer.seconds() < 0.1) {
//                    turret.shoot(turret.currentPower);
//                }else{
//                    turret.shoot(1);
//                }
//            }, 1)

//            stage(turret, () -> turret.shoot(0.75), 2),
//            stage(turret, intake,  () -> {turret.shoot(0.9); intake.feed(1.0); intake.intake(1.0); }, 2)
//            stage(turret, () -> turret.shoot(0.8), 2),
//            stage(turret, intake,  () -> {turret.shoot(0.9); intake.feed(1.0); }, 0.2),
//            stage(turret, () -> turret.shoot(0.8), 2),
//            stage(turret, intake,  () -> {turret.shoot(0.9); intake.feed(1.0); }, 0.2)
    );


    ChainMaker ShootFar = () -> new Chain(
            stage(intake, () -> QbitOp.isTurret23Mode.set(false)),
            stage(intake, intake.setFeedTargetRelative(-360, 1), () -> !intake.feeder.isMotorAtTarget()),
            stage(intake, intake::unlock, 0.2),
            stage(intake, intake.resetFeedRunMode()),
            stage(intake, () -> {}, intake.isReady),
            stage(intake, () -> {}, turret.isNotReady),
            stage(intake, () -> intake.intakeAndFeed(0.5), 0.5),
            stage(intake, () -> QbitOp.isTurret23Mode.set(true)),
            stage(intake, () -> intake.intakeAndFeed(0.5), 2.0),
            stage(intake, () -> QbitOp.isTurretTargeting.set(false)),
            stage(intake, () -> QbitOp.isTurret23Mode.set(false))
    );


    ChainMaker ShootAuto = () -> new Chain(
//            stage(intake, intake::lock, 0.1),
            stage(intake, () -> intake.feed(-1), 0.05),
            stage(intake, intake::unlock, 0.1),
            stage(intake, () -> {}, turret.isNotReady),
            stage(intake, () -> QbitOp.isTurret23Mode.set(true)),
            stage(intake, () -> intake.intakeAndFeed(1), 1.5),
            stage(intake, () -> QbitOp.isTurretTargeting.set(false)),
            stage(intake, () -> QbitOp.isTurret23Mode.set(false))
    );


 ChainMaker JustIntake = () -> new Chain(
         stage(intake, intake::unlock, 0.1),
         stage(intake, () -> intake.intakeAndFeed(1), 1)

         );
//    ChainMaker Shoot2 = () -> new Chain(
//
//            // Start intake and feeder (NO TIME LIMIT)
//            stage(intake, () -> intake.intakeAndFeed(1)),
//
//            // Set shooter RPM immediately
//            stage(turret, () -> {
//                double rpm = turret.getShooterRPMFromLimelight();
//                QbitOp.shooterTarget.set(rpm);
//            }),
//
//            // WAIT for turret/shooter to be ready
//            stage(turret, () -> {}, turret.isNotReady),
//
//            // Continue feeding for shooting time
//            stage(intake, () -> intake.intakeAndFeed(1), 1.5),
//
//            // Stop intake
//            stage(intake, intake::stop),
//
//            // Stop targeting
//            stage(intake, () -> QbitOp.isTurretTargeting.set(false))
//    );
}