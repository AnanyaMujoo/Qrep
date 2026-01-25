package robotparts.electronics;


import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import global.Log;
import utility.Timer;

public class PositionalServo extends Electronic {

    private final Servo servo;

    public PositionalServo(Servo s, Servo.Direction dir){
        servo = s;
        servo.setDirection(dir);
    }

    public void scaleRange(double lower, double upper){ servo.scaleRange(lower, upper); }

    public void moveTo(double pos){ servo.setPosition(pos); }

    public double getPosition(){
        return servo.getPosition();
    }

}
