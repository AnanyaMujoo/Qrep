package robotparts.electronics;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class Motor extends Electronic {
    /**
     * DcMotor object since its continuous
     */
    protected final DcMotorEx motor;
    /**
     * Constructor with parameters
     * @param m
     * @param dir
     * @param zpb
     */
    public Motor(DcMotorEx m, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zpb){
        motor = m;
        motor.setDirection(dir);
        motor.setZeroPowerBehavior(zpb);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motor.setPower(0);
    }

    /**
     * Enable stall detector
     */
    /**
     * Sets the power of the motor if access is allowed
     * @param p
     */
    public void setPower(double p){
        if(access.doesCurrentThreadHaveAccess()){
            motor.setPower(p);
        }
    }

    /**
     * Gets the direction of the motor
     * @return direction
     */
    @Override
    public void stop(){ setPower(0); }

}
