package robotparts.hardware.templates;

import robotparts.RobotPart;
import robotparts.electronics.MotorWithEncoder;

public class LiftTemplate extends RobotPart {

    public MotorWithEncoder right, left;

    @Override
    public void init() {
        right = createMotorWithEncoder("lil", MOTOR_FORWARD, MOTOR_BRAKE, false,
                2.4, 1.0, 30,
                -0.05, 0.5, 1);
        left = createMotorWithEncoder("lir", MOTOR_REVERSE, MOTOR_BRAKE, false,
                2.4, 1.0, 30,
                -0.05, 0.5, 1);

    }

    public void setTarget(double target, double power){
        right.setTarget(target, power);
        left.setTarget(target, power);
    }

    public void adjustTarget(double offset, double power){
        right.adjustTarget(offset, power);
        left.adjustTarget(offset, power);
    }

    public void softResetEncoders(){
        right.softResetEncoder();
        left.softResetEncoder();
    }

    public double getRightPosition(){ return right.getPosition(); }
    public double getLeftPosition(){ return left.getPosition(); }
    public double getAveragePosition(){ return (getLeftPosition() + getRightPosition())/2.0; }


}
