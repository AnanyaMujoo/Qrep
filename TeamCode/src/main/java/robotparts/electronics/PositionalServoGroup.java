package robotparts.electronics;

import java.util.ArrayList;
import java.util.Arrays;

import global.Log;

public class PositionalServoGroup {
    private final ArrayList<PositionalServo> servos;

    public PositionalServoGroup(PositionalServo... servos){
        this.servos = new ArrayList<>(Arrays.asList(servos));
    }

    public void moveTo(double position){
        servos.forEach(servo -> servo.moveTo(position));
    }

    public void moveTo(double... positions) {
        if (positions.length != servos.size()) {
            Log.error("Number of positions must match number of servos in PositionalServo.Group");
        }
        for (int i = 0; i < servos.size(); i++) {
            servos.get(i).moveTo(positions[i]);
        }
    }
}
