package robotparts.electronics;

import static global.Common.allMotorWithEncoderRotational;
import static global.Common.allMotorWithEncoders;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import controllers.PositionHolder;
import global.Constants;
import global.Log;

public class MotorWithEncoderRotational extends Motor {
    public final Encoder encoder;
    private final double degreesToTicks;
    private final AtomicInteger target = new AtomicInteger(0);
    private final AtomicReference<Double> powerScale = new AtomicReference<>(0.0); //0-100

    public MotorWithEncoderRotational(DcMotorEx m, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zpb,
                                      boolean invertedEncoder, double encoderPPR, double motorToOutputGearRatio) {
        super(m, dir, zpb);
        encoder = new Encoder(m, invertedEncoder);
        target.set(0);
        powerScale.set(0.0);
        this.degreesToTicks =  (1.0/360.0) * encoderPPR * (motorToOutputGearRatio);
        allMotorWithEncoderRotational.get().add(this);
    }

    public double getPower() { return motor.getPower(); }
    public boolean isMotorAtTarget(){
        return !motor.isBusy();
    }
    public double getPosition(){ return (1/degreesToTicks)*(encoder.getPosition()); }

    public double getPositionRaw(){ return (1/degreesToTicks)*(encoder.getPositionRaw()); }
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
        setTarget(getTarget()+offset, power);
    }




    public void setTarget(double target, double power){
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double targetInTicks = target * degreesToTicks;
        double adjustedTarget = targetInTicks + encoder.getStartPosition();
        motor.setTargetPosition((int) adjustedTarget);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.target.set((int) target);
        this.powerScale.set(power);
        motor.setPower(power);
    }

    public void setTargetVelocity(double targetVelocity){
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double velocityInTicks = targetVelocity * degreesToTicks * 360/60;
        motor.setVelocity(velocityInTicks);


//        double targetInTicks = target * degreesToTicks;
//        double adjustedTarget = targetInTicks + encoder.getStartPosition();
//        motor.setTargetPosition((int) adjustedTarget);
//        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        this.target.set((int) target);
//        this.powerScale.set(power);
//        motor.setPower(power);
    }

    public double getTarget() {
        return target.get();
    }
    public double getPowerScale() {
        return powerScale.get();
    }
    public void updateEncoderPosition(){
        encoder.updatePosition();
    }

    public double getVelocity(){
        return (motor.getVelocity()/degreesToTicks)  * 60/360;
    }


    /**
     * Set PIDF Coefficients for Velocity Control
     */
    public void setPIDF(double p, double i, double d, double f) {
        // Cast to Ex to access PIDF features
        DcMotorEx motorEx = (DcMotorEx) motor;

        // Create the new coefficients
        PIDFCoefficients newPIDF = new PIDFCoefficients(p, i, d, f);

        // Force the motor into the correct mode for Velocity PID
        motorEx.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send the values to the hardware
        motorEx.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, newPIDF);
    }

    /**
     * Get the REAL coefficients from the motor (to verify they saved)
     */
    public PIDFCoefficients getPIDF() {
        return ((DcMotorEx) motor).getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
