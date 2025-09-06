package robotparts.electronics;

import static global.Common.allMotorWithEncoders;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

import java.util.concurrent.atomic.AtomicInteger;

import controllers.PositionHolder;
import global.Constants;
import global.Log;

public class MotorWithEncoder extends Motor {
    public final Encoder encoder;
    private final double distanceToTicks;
    public final PositionHolder positionHolder;
    private final AtomicInteger target = new AtomicInteger(0);
    private final AtomicInteger powerScale = new AtomicInteger(0); //0-100
    private final double maximumDistance;


    public MotorWithEncoder(DcMotorEx m, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zpb,
                            boolean invertedEncoder, double pulleyRadius, double motorToPulleyGearRatio,
                            double maximumDistance, double snapToZeroPower, double snapToZeroTime,
                            double snapToZeroDistance) {
        super(m, dir, zpb);
        encoder = new Encoder(m, invertedEncoder);
        target.set(0);
        powerScale.set(0);
        this.maximumDistance = maximumDistance;
        this.distanceToTicks =  (1/(2 * Math.PI * pulleyRadius)) * Constants.ORBITAL_ENCODER_TICKS_PER_REVOLUTION * (motorToPulleyGearRatio);
        positionHolder = new PositionHolder(this, snapToZeroPower, snapToZeroTime, snapToZeroDistance);
        allMotorWithEncoders.get().add(this);
    }

    public double getPower() { return motor.getPower(); }
    public boolean isMotorAtTarget(){
        return !motor.isBusy();
    }
    public double getPosition(){ return (1/distanceToTicks)*(encoder.getPosition()); }
    public boolean isMotorInTargetingMode(){ return motor.getMode().isPIDMode(); }
    public void resetRunMode(){
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void softResetEncoder(){
        target.set(0);
        encoder.softReset();
    }

    public void hardResetEncoder(){
        target.set(0);
        encoder.hardReset();
    }

    public void adjustTarget(double offset, double power){
        setTarget(Range.clip(getTarget()+offset, 0, maximumDistance), power);
    }




    public void setTarget(double target, double power){
        if(target > maximumDistance || target < 0){
            Log.error("Cannot set target of MotorWithEncoder greater than maximumDistance or less than 0");
        }
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double targetInTicks = target * distanceToTicks;
        double adjustedTarget = targetInTicks + encoder.getStartPosition();
        motor.setTargetPosition((int) adjustedTarget);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.target.set((int) target);
        this.powerScale.set((int) (power * 100));
        positionHolder.holdingPosition.set(true);
    }
    public double getTarget() {
        return target.get();
    }
    public double getPowerScale() {
        return (double) powerScale.get() /100;
    }

    public void updateEncoderPosition(){
        encoder.updatePosition();
    }

    public void updatePositionHolder(){
        positionHolder.updatePositionHolder();
    }

}
