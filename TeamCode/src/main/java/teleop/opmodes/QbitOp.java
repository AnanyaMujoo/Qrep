package teleop.opmodes;

import static global.Common.gamepad2;
import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.intake;
import static robotparts.RobotConfig.turret;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import geometry.Pose;
import global.Common;
import robotparts.hardware.Turret;
import teleop.Tele;
import teleop.teleutil.Button;

@TeleOp
public class QbitOp extends Tele {

    public static AtomicReference<Double> shooterTarget = new AtomicReference<>(0.0);
    public static AtomicReference<Double> oldTarget = new AtomicReference<>(0.0);


    public static AtomicReference<Double> turnError = new AtomicReference<>(0.0);

    public static AtomicBoolean isTurretTargeting = new AtomicBoolean(false);
    public static AtomicBoolean isTurret23Mode = new AtomicBoolean(false);

    @Override
    public void initTele() {

//        gpA.onClickToggle(Button.RIGHT_BUMPER, () -> isTurretTargeting.set(true), () -> isTurretTargeting.set(false));

//        gpA.onClick(Button.RIGHT_BUMPER, () -> intake.lock());
//        gpA.onClick(Button.LEFT_BUMPER, () -> intake.unlock());

//        gpB.onClickToggle(Button.X, () -> turret.shoot(0.7), () -> turret.shoot(0) );
        gpA.onClick(Button.Y, Intake);
        gpA.onClick(Button.X, Shoot);
//        gpA.onClick(Button.Y, intake.setFeedTargetRelative(180, 1.0));
//        intake.feeder.softResetEncoder();

//        gpA.onClick(Button.B, () -> shooterTarget.set(3000.0));




        intake.lock();
        turret.shooter.softResetEncoder();

        shooterTarget.set(0.0);
        oldTarget.set(0.0);
        isTurretTargeting.set(false);
        isTurret23Mode.set(false);

        turnError.set(0.0);
    }

    @Override
    public void startTele() {
//        turret.limey.start();
    }

    @Override
    public void loopTele() {


        drive.move(0.7*gpA.ry, 0.7*gpA.rx, 0.6*gpA.lx);

        drive.updateOdometry();
        display("Odo X (cm)", drive.getX());
        display("Odo Y (cm)", drive.getY());
        display("Odo H (deg)", drive.getHeading());

//        drive.updateOdotest();
//        display("Odo X (mm)", drive.getOdoXMM());
//        display("Odo Y (mm)", drive.getOdoYMM());
//        display("Odo H (deg)", drive.getOdoHeadingDeg());




//        turret.shoot(0.6);




//        intake.intake(gpB.ry);
//        intake.feed(gpB.ly);
//        if(gpB.rt > 0){
//            intake.intake(1);
//            intake.feed(1);
//        }else if(gpB.lt > 0){
//            intake.intake(-1);
//            intake.feed(-1);
//        }else{
//            intake.intake(0);
//            intake.feed(0);
//        }
//        if(gpB.rt > 0 || gpB.lt > 0){
//            turret.shoot(gpB.rt - gpB.lt);
//        }else{
//            turret.shoot(0);
//        }

        display("Detect", intake.getDetectDistance());



//        display("Speed", turret.shooter.getVelocity());
//        display("Power", turret.shooter.getPower());




        if(isTurretTargeting.get()){
            Pose pose = turret.getPoseWithLimey();
            double distance = pose.getY();
            double angle = pose.getAngle();

            if(distance > 80){
                double targetAngle = Math.toDegrees(Math.atan(Turret.LIMEY_LEFT_DISTANCE/distance));
                double error = targetAngle - angle;
                turnError.set(error);

                display("Angle", angle);
                display("TargetAngle", targetAngle);
                display("Distance", distance);
                display("Error", error);

                double power = -Math.signum(error)*Turret.TURRET_TARGETING_REST_POWER - error*Turret.TURRET_TARGETING_K;

                if(Math.abs(error) > 1) {
                    turret.turn(power);
                }else{
                    turret.turn(0.0);
                }

                double realDistance = distance*Math.cos(Math.toRadians(targetAngle))/100;


                double t = Math.sqrt(2 * (realDistance * Math.tan(Math.toRadians(Turret.SHOOT_ANGLE)) - (Turret.HEIGHT_DIFFERENCE/100))/Turret.g);

                double v = (realDistance/t) / Math.cos(Math.toRadians(Turret.SHOOT_ANGLE));

                double rpm = 60*(2*v)/(Math.PI*0.1016);

                display("v", v);
                display("rpm", rpm);
                if(!isTurret23Mode.get()) {
                    shooterTarget.set(rpm * Turret.SHOOT_RATIO_1);
                }else{
                    shooterTarget.set(rpm * Turret.SHOOT_RATIO_23);
                }
            }else{
                turret.turn(0.0);
                shooterTarget.set(2000.0);
            }
        }else{
            turret.turn(0.0);
            shooterTarget.set(0.0);

            display("Inhere");
        }

        display("turretTarhteting", isTurretTargeting.get());


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
    public void stopTele() {
        turret.limey.stop();
    }
}
