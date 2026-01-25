package teleop.opmodes;

import static global.Common.gamepad2;
import static global.General.fieldSide;
import static robotparts.RobotConfig.drive;
import static robotparts.RobotConfig.intake;
import static robotparts.RobotConfig.turret;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import elements.FieldSide;
import geometry.Pose;
import global.Common;
import robotparts.hardware.Turret;
import teleop.Tele;
import teleop.teleutil.Button;
import utility.Timer;

@TeleOp
public class QbitOp extends Tele {

    public static AtomicReference<Double> shooterTarget = new AtomicReference<>(0.0);
    public static AtomicReference<Double> oldTarget = new AtomicReference<>(0.0);


    public static AtomicReference<Double> turnError = new AtomicReference<>(0.0);

    public static AtomicBoolean isTurretTargeting = new AtomicBoolean(false);
    public static AtomicBoolean isTurret23Mode = new AtomicBoolean(false);
    private double ratio12 = 1.05;
    private double ratio23 = 1.10;
    public static AtomicBoolean readyToShoot = new AtomicBoolean(false);


    public static AtomicBoolean farMode = new AtomicBoolean(false);


    public ArrayList<Double> angleArray = new ArrayList<>();

    public Timer timer = new Timer();

    @Override
    public void initTele() {

//        gpA.onClickToggle(Button.RIGHT_BUMPER, () -> isTurretTargeting.set(true), () -> isTurretTargeting.set(false));

//        gpA.onClick(Button.RIGHT_BUMPER, () -> intake.lock());
//        gpA.onClick(Button.LEFT_BUMPER, () -> intake.unlock());

//        gpB.onClickToggle(Button.X, () -> turret.shoot(0.7), () -> turret.shoot(0) );
        turret.shooter.setPIDF(25, 0, 0.00000, 13.5);
//        turret.turret.setPIDF(6, 0, 0, 14);

        gpA.onClick(Button.Y, Intake);
        gpA.onClick(Button.X, () -> {
            if(farMode.get()){
                ShootFar.run();
            }else{
                Shoot.run();
            }
        });
//        gpA.onClick(Button.X, Shoot2);
        gpA.onClick(Button.B, JustIntake);
        gpA.onClick(Button.DPAD_UP, ()-> Turret.SHOOT_OFFSET_1 +=50.0);
        gpA.onClick(Button.DPAD_DOWN, ()-> Turret.SHOOT_OFFSET_1 -=50.0);
        gpA.onClick(Button.DPAD_RIGHT, ()-> Turret.SHOOT_OFFSET_23+=50.0);
        gpA.onClick(Button.DPAD_LEFT, ()-> Turret.SHOOT_OFFSET_23-=50.0);
//        gpA.onClick(Button.Y, intake.setFeedTargetRelative(180, 1.0));
//        intake.feeder.softResetEncoder();

//        gpA.onClick(Button.B, () -> shooterTarget.set(3000.0));

        readyToShoot.set(false);
        turret.turret.softResetEncoder();

        farMode.set(false);


        intake.lock();
        turret.shooter.softResetEncoder();

        shooterTarget.set(0.0);
        oldTarget.set(0.0);
        isTurretTargeting.set(false);
        isTurret23Mode.set(false);

        turnError.set(0.0);

        angleArray = new ArrayList<>();
        angleArray.add(0.0);
        angleArray.add(0.0);

        timer.reset();
    }


    @Override
    public void startTele() {
//        turret.limey.start();
    }

    @Override
    public void loopTele() {


        drive.move(0.7*gpA.ry, 0.7*gpA.rx, 0.6*gpA.lx);

        drive.updateOdometry();
//        display("Odo X (cm)", drive.getX());
//        display("Distance", turret.getDistance());
//        display("Odo Y (cm)", drive.getY());
//        display("Odo H (deg)", drive.getHeading());
//        display("Odo X | Y | H", String.format("%.1f | %.1f | %.1f", drive.getX(), drive.getY(), drive.getHeading()));
//        display("Color Sensor Dist", intake.getDetectDistance());
//        display("Ball Detected?", intake.getDetectDistance() < 4.1);
        display("Target", Math.round(shooterTarget.get()));
//        display("Actual", Math.round(turret.shooter.getVelocity()));
        display("Distance", turret.getDistance());
        display("Offset1", Turret.SHOOT_OFFSET_1);
        display("Offset23", Turret.SHOOT_OFFSET_23);
//        display("distance", turret.getDistance());
//        display("distance color sensor",intake.getDetectDistance());

        display("READY TO BLITZ", readyToShoot.get() ? "!!! YES !!!" : "AIMING...");

//        display("Shooter Target | Actual", shooterTarget.get() + " | " + turret.shooter.getVelocity());
//        display("Turn Error", turnError.get());
//        display("READY TO BLITZ", readyToShoot.get() ? "!!! YES !!!" : "AIMING...");
//        display("angle", turret.turret.getPosition());
//        display("shootertqarget", shooterTarget.get());
//        display("shooter velocity", turret.shooter.getVelocity());
//        display("shooter velocity error", shooterTarget.get()-turret.shooter.getVelocity());
//        display("shooter poition ", turret.shooter.getPosition());
//
//
//
//        display(" turn error", turnError.get());
//        display("pidf", turret.shooter.getPIDFCoefficients());




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

//        display("Detect", intake.getDetectDistance());



//        display("Speed", turret.shooter.getVelocity());
//        display("Power", turret.shooter.getPower());




        if(isTurretTargeting.get()){
            Pose pose = turret.getPoseWithLimey();
            double distance = pose.getY();
            double angle = pose.getAngle();
//            display("Angle", angle);
//            display("Distance", distance);
            double targetAngle = Math.toDegrees(Math.atan(Turret.LIMEY_LEFT_DISTANCE/distance));
            double error = targetAngle - angle;
//            display("TargetAngle", targetAngle);
//            display("Error", error);

            if(distance > 80){
                targetAngle = Math.toDegrees(Math.atan(Turret.LIMEY_LEFT_DISTANCE/distance));
                //Todo fix

//
//                angleArray.add(angle);
//                int last = angleArray.size()-1;
//                double averageAngle = (angleArray.get(last) + angleArray.get(last-1) + angleArray.get(last-2))/3.0;

                error = targetAngle - (angle);

                turnError.set(error);

//                double targetRPM = turret.getShooterRPMFromLimelight();

//                double actual = turret.shooter.getVelocity();

//                readyToShoot.set(Math.abs(error) < 1.5 && Math.abs(actual - targetRPM) < 120);




                if(timer.seconds() > 0.5 && Math.abs(error) > 0.5) {
                    turret.turret.softResetEncoder();
                    turret.turret.setTarget(-error, 0.1);
                    timer.reset();
                }

//                display("Angle", angle);
//                display("TargetAngle", targetAngle);
//                display("Distance", distance);
//                display("Error", error);
//                display("Velocity Error", turret.errorReturn());
//                double power = (-Math.signum(error)*Turret.TURRET_TARGETING_REST_POWER - error*Turret.TURRET_TARGETING_K)*0.5;
//
//                if(Math.abs(error) > 1) {
//                    turret.turn(power);
//                }else{
//                    turret.turn(0.0);
//                }

//                double realDistance = distance*Math.cos(Math.toRadians(targetAngle))/100;
//
//
//                double t = Math.sqrt(2 * (realDistance * Math.tan(Math.toRadians(Turret.SHOOT_ANGLE)) - (Turret.HEIGHT_DIFFERENCE/100))/Turret.g);
//
//                double v = (realDistance/t) / Math.cos(Math.toRadians(Turret.SHOOT_ANGLE));
//
//                double rpm = 60*(2*v)/(Math.PI*0.1016);
//
//
//
//                display("v", v);
//                display("rpm", rpm);
//                if(!isTurret23Mode.get()) {
//                    shooterTarget.set(rpm * Turret.SHOOT_RATIO_1);
//                }else{
//                    shooterTarget.set(rpm * Turret.SHOOT_RATIO_23);
//                }


                if(!isTurret23Mode.get()) {
                    shooterTarget.set(turret.getRPM1(distance) + Turret.SHOOT_OFFSET_1);
                }else{
                    shooterTarget.set(turret.getRPM23(distance) + Turret.SHOOT_OFFSET_23);
                }

                farMode.set(distance > 500);

//                if(distance > 400 && !farMode.get()){
//                    farMode.set(true);
////                    Turret.SHOOT_RATIO_1 = 1;
////                    Turret.SHOOT_RATIO_23 = 1;
//                }else if(distance < 400 && farMode.get()){
//                    farMode.set(false);
//                }

//                display("TargetRPM", targetRPM);

                display("Farmode", farMode.get());

//                display("RPM", targetRPM);
            }else{
                turret.turn(0.0);
                shooterTarget.set(2000.0);
                readyToShoot.set(false);

            }
        }else{
            turret.turn(0.0);
            shooterTarget.set(0.0);
            turret.turret.resetRunMode();
//
//            display("Inhere");
        }

//
//        display("turretTarhteting", isTurretTargeting.get());


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
    @TeleOp(name = "BlueTeleOp", group = "TeleOp")
    public static class BlueTeleOp extends QbitOp {{fieldSide = FieldSide.BLUE; }}

    @TeleOp(name = "RedTeleOp", group = "TeleOp")
    public static class RedTeleOp extends QbitOp {{fieldSide = FieldSide.RED; }}
}
