package controllers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import robotparts.electronics.Encoder;
import robotparts.electronics.MotorWithEncoder;
import utility.Timer;

public class PositionHolder {
    public final AtomicBoolean holdingPosition = new AtomicBoolean(false);
    private final Timer timer = new Timer();
    private final double snapToZeroPower;
    private final double snapToZeroTime;
    private final double snapToZeroDistance;
    private final MotorWithEncoder motorWithEncoder;

    public PositionHolder(MotorWithEncoder motorWithEncoder, double snapToZeroPower, double snapToZeroTime, double snapToZeroDistance) {
        this.motorWithEncoder = motorWithEncoder;
        this.snapToZeroPower = snapToZeroPower;
        this.snapToZeroTime = snapToZeroTime;
        this.snapToZeroDistance = snapToZeroDistance;
        holdingPosition.set(false);
        timer.reset();
    }

    public void updatePositionHolder() {
        if (holdingPosition.get()) {
            double position = motorWithEncoder.getPosition();
            if (position < snapToZeroDistance && motorWithEncoder.getTarget() < snapToZeroDistance) {
                holdingPosition.set(false);
            }else {
                motorWithEncoder.setPower(motorWithEncoder.getPowerScale());
            }
        } else {
            if(motorWithEncoder.isMotorInTargetingMode()){
                motorWithEncoder.resetRunMode();
                timer.reset();
            }
            if(timer.seconds() < snapToZeroTime) {
                motorWithEncoder.setPower(snapToZeroPower);
            }else{
                motorWithEncoder.stop();
            }
        }
    }

}




