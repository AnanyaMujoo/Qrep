package robotparts.hardware.templates;

import com.qualcomm.robotcore.hardware.Servo;

import robotparts.RobotPart;
import robotparts.electronics.PositionalServoGroup;

public class OuttakeTemplate extends RobotPart {

    public PositionalServoGroup flipBoth, clawBoth;

    @Override
    public void init() {
        flipBoth = createPositionalServoGroup("flipl", SERVO_FORWARD, "flipr", SERVO_REVERSE);
        clawBoth = createPositionalServoGroup("clawl", SERVO_FORWARD, "clawr", SERVO_REVERSE);
    }

    public void flipStart(){ flipBoth.moveTo(0.2); }
    public void flipMiddle(){ flipBoth.moveTo(0.5); }
    public void flipEnd(){ flipBoth.moveTo(0.8); }

    public void clawOpen(){ clawBoth.moveTo(0.45, 0.48); }
    public void clawClose(){ clawBoth.moveTo(0.55, 0.59); }

    public void moveOuttakeToStart(){ clawOpen(); flipStart(); }
    public void moveOuttakeToEnd(){ clawClose(); flipEnd(); }
}
