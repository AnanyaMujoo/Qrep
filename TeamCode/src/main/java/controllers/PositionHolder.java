package controllers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import robotparts.electronics.Encoder;
import robotparts.electronics.MotorWithEncoder;

public class PositionHolder {
    public final AtomicBoolean holdingPosition = new AtomicBoolean(false);
    private double snapToZeroPower = -0.05;
    private double snapToZeroDistance = 1;// cm
    private MotorWithEncoder motorWithEncoder;


    public PositionHolder() {
    }

    public void initializePositionHolder(MotorWithEncoder motorWithEncoder, double snapToZeroPower, double snapToZeroDistance) {
        this.motorWithEncoder = motorWithEncoder;
        this.snapToZeroPower = snapToZeroPower;
        this.snapToZeroDistance = snapToZeroDistance;
        holdingPosition.set(false);
    }

    public void updatePositionHolder() {
        if (holdingPosition.get()) {
            double position = motorWithEncoder.getPosition();
            if (position < snapToZeroDistance && motorWithEncoder.getTarget() < snapToZeroDistance) {
                holdingPosition.set(false);
            }
            motorWithEncoder.setPower(motorWithEncoder.getPowerScale());
        }

        else {
            motorWithEncoder.setPower(snapToZeroPower);
        }

//TODO Test if oscikllation is unbearable

    }

}




