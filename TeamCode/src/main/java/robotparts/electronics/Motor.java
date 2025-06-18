package robotparts.electronics;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class Motor extends Electronic {
    /**
     * DcMotor object since its continuous
     */
    private final DcMotorEx motor;
    private final Encoder motorEncoder;
    /**
     * Constructor with parameters
     * @param m
     * @param dir
     * @param zpb
     */
    public Motor(DcMotorEx m, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zpb){
        motor = m;
        motorEncoder = new Encoder(motor, Encoder.EncoderType.CMOTOR);
        motor.setDirection(dir);
        motor.setZeroPowerBehavior(zpb);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motor.setPower(0);
    }

    public DcMotor getMotor(){
        return motor;
    }

    /**
     * Enable stall detector
     */
    public void useStallDetector(){
        motorEncoder.setUpdateCMotor();
    }

    /**
     * Sets the power of the motor if access is allowed
     * @param p
     */
    public void setPower(double p){
        if(access.doesMainThreadHaveAccess()){
            if(!detector.isStalling()){
                motor.setPower(Precision.clip(p, 1)*voltageScale);
            }else{
                motor.setPower(0);
                fault.warn("Motor is stalling, stopped all AutoModules", Expectation.EXPECTED, Magnitude.CRITICAL);
                bot.cancelAutoModules();
            }
        }
    }

    public void setPowerRaw(double power){ motor.setPower(Precision.clip(power, 1)); }

    /**
     * Gets the direction of the motor
     * @return direction
     */
    public DcMotorSimple.Direction getDirection(){
        return direction;
    }

    public Encoder getMotorEncoder(){ return motorEncoder; }

    /**
     * Sets the power of the motor to 0
     * NOTE: This should only be called in a thread that has access to use the robot
     */
    public void halt(){ setPower(0); }

    public StallDetector getStallDetector() {
        return detector;
    }

}
