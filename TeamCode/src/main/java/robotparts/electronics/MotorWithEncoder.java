package robotparts.electronics;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.concurrent.atomic.AtomicInteger;

import controllers.PositionHolder;
import global.Constants;

public class MotorWithEncoder extends Motor {
    private final Encoder encoder;
    private boolean properlyInitializedWithParameters = false;
    private double distanceToTicks = 1;
    private final PositionHolder positionHolder;
    private final AtomicInteger target = new AtomicInteger(0);
    private final AtomicInteger powerScale = new AtomicInteger(0); //0-100


    public MotorWithEncoder(DcMotorEx m, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zpb, boolean invertedEncoder) {
        super(m, dir, zpb);
        encoder = new Encoder(m, invertedEncoder);
        properlyInitializedWithParameters = false;
        target.set(0);
        powerScale.set(0);
        positionHolder = new PositionHolder();
    }
    //TODO MAKE METHODS OT MANUAL ADJUST LLIL BUD

    public MotorWithEncoder setParameters(double snapToZeroPower, double snapToZeroDistance, double pulleyRadius, double motorToPulleyGearRatio, double angleToVertical, double ticksPerRevolution) {
        this.distanceToTicks =  (1/(2 * Math.PI * pulleyRadius)) * Constants.ORBITAL_ENCODER_TICKS_PER_REVOLUTION * (motorToPulleyGearRatio);
        properlyInitializedWithParameters = true;
        positionHolder.initializePositionHolder(this, snapToZeroPower, snapToZeroDistance);
        return this;
    }

    public boolean isProperlyInitialized(){
        return properlyInitializedWithParameters;
    }

    public double getPower() { return motor.getPower(); }

    public boolean isMotorAtTarget(){
        return !motor.isBusy();
    }

    public double getPosition(){ return (1/distanceToTicks)*(encoder.getPosition()); }

    public void stopAndResetRunMode(){
        stop();
        resetRunMode();
    }
    public void resetRunMode(){
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void softResetEncoder(){
        encoder.softReset();}
    public void hardResetEncoder(){
        encoder.hardReset();}


    //TODO we want to make a method to move the lift in increments and have position holder always running, and it updates the tagret based on manual joystick/dpad
    //TODO everyone commanding posiition holder

    public final void setTarget(double target, double power){
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motor.setTargetPosition(
                (int) (target*distanceToTicks)
        );
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.target.set((int) target);
        this.powerScale.set((int) (power*100));
        positionHolder.holdingPosition.set(true);
    }
    public double getTarget() {
        return target.get();
    }
    public double getPowerScale() {
        return (double) powerScale.get() /100;
    }










////    public void moveWithPositionHolder(double power) {
//        if(power != 0){
////            log.show("Manual movement");
//            // Manual movement (no position holding, yes rest power)
//            positionHolder.deactivate();
//            move(power + restPowerFunction.run(getPosition()));
//        }else if (lastTarget != 0){
////            log.show("Not moving, nonzero target");
//            // Not moving, nonzero target (yes position holding, yes rest power)
//            positionHolder.activate();
//            move(restPowerFunction.run(getPosition()));
//        }else if(getPosition() > snapToZeroDistance){
////            log.show("Above snap range");
//            // Above snap range (any position holding, yes rest power
//            move(restPowerFunction.run(getPosition()));
//        }else if(getPosition() > 0.2 && getPosition() < snapToZeroDistance){
////            log.show("In snap range");
//            // In snap range (no position holding, no rest power, yes down power)
//            positionHolder.deactivate();
//            move(-Math.abs(snapToZeroPower));
//        }else{
////            log.show("Below snap range");
//            // Below snap range (no position holding, no rest power, no down power)
//            positionHolder.deactivate();
//            move(0.0);
//        }

}
