package robotparts.electronics;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.concurrent.atomic.AtomicBoolean;

import global.Constants;

public class MotorWithEncoder extends Motor {
    private final Encoder encoder;
    private double lastTarget;
    private final AtomicBoolean holdingPosition = new AtomicBoolean(false);
    private boolean properlyInitializedWithParameters = false;
    private double snapToZeroPower = 0.05;
    private double snapToZeroDistance = 1;// cm
    private double distanceToTicks = 1;

    /**
     * Constructor with parameters
     *
     * @param m
     * @param dir
     * @param zpb
     */
    public MotorWithEncoder(DcMotorEx m, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zpb, boolean invertedEncoder) {
        super(m, dir, zpb);
        encoder = new Encoder(m, invertedEncoder);
        lastTarget = 0;
        releasePosition();
        properlyInitializedWithParameters = false;
    }//TODO goal: 2 different PID contorllers (positional, hold), want only 1, not use the manual

    public MotorWithEncoder setParameters(double snapToZeroPower, double snapToZeroDistance, double pulleyRadius, double motorToPulleyGearRatio, double angleToVertical, double ticksPerRevolution) {
        this.snapToZeroPower = snapToZeroPower;
        this.snapToZeroDistance = snapToZeroDistance;
        this.distanceToTicks =  (1/(2 * Math.PI * pulleyRadius)) * Constants.ORBITAL_ENCODER_TICKS_PER_REVOLUTION * (motorToPulleyGearRatio);
        properlyInitializedWithParameters = true;
        return this;
    }

    public boolean isProperlyInitialized(){
        return properlyInitializedWithParameters;1
    }

    public void releasePosition(){
        holdingPosition.set(false);
    }

    /**
     * Get the position holder object
     * @return position holder
     */
    public void holdPosition(){
        holdingPosition.set(true);
    }

    public double
    //TODO Decide what to do with lift extend and poisition holder logic and make move method
    /**
     * Set the position to move to
     * @param target
     */
    public final void setTarget(double target){
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motor.setTargetPosition(
                (int) (target*distanceToTicks)
        );
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lastTarget = target;
    }


    /**
     * Get the target position
     * @return target
     */
    public double getTarget() {
        return lastTarget;
    }

    /**
     * Get the power of the motor
     * @return power
     */
    public double getPower() { return motor.getPower(); }

    /**
     * Has the motor reached the target
     * @return if the motor is not busy (done) or is stalling (prevent damage)
     */
    public boolean isMotorAtTarget(){
        return !motor.isBusy();
    }

    /**
     * Get the position of the motor
     * @return output
     */
    public double getPosition(){ return (1/distanceToTicks)*(encoder.getPosition()); }

    /**
     * Stop and reset the mode of the pmotor
     */
    public void stopAndResetRunMode(){
        stop();
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Reset the position of the pmotor
     */
    public void softResetEncoder(){
        encoder.softReset();}
    public void hardResetEncoder(){
        encoder.hardReset();}


    /**
     * Default move with position holder
     * @param power
     */
    public void moveWithPositionHolder(double power) {
        if(power != 0){
//            log.show("Manual movement");
            // Manual movement (no position holding, yes rest power)
            positionHolder.deactivate();
            move(power + restPowerFunction.run(getPosition()));
        }else if (lastTarget != 0){
//            log.show("Not moving, nonzero target");
            // Not moving, nonzero target (yes position holding, yes rest power)
            positionHolder.activate();
            move(restPowerFunction.run(getPosition()));
        }else if(getPosition() > snapToZeroDistance){
//            log.show("Above snap range");
            // Above snap range (any position holding, yes rest power
            move(restPowerFunction.run(getPosition()));
        }else if(getPosition() > 0.2 && getPosition() < snapToZeroDistance){
//            log.show("In snap range");
            // In snap range (no position holding, no rest power, yes down power)
            positionHolder.deactivate();
            move(-Math.abs(snapToZeroPower));
        }else{
//            log.show("Below snap range");
            // Below snap range (no position holding, no rest power, no down power)
            positionHolder.deactivate();
            move(0.0);
        }
    }
    /**
     * Type of movement preformed
     */


}
