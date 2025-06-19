package robotparts.electronics;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.List;

public class Encoder extends Electronic {
    /**
     * Encoder class, note that the following naming convention should be used
     * [motor name]Enc
     *
     * Ex: If the motor was named bl, the encoder name would be blEnc
     */

    /**
     * Motor that the encoder refrences
     */
    private final DcMotorEx motor;
    /**
     * Type of encoder
     * @link Type
     */

    private double position, startPosition = 0; // ticks
    private final boolean invertedOutput;

    public static void setHubsToBulkRead(HardwareMap hardwareMap){
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule module : allHubs) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }
    /**
     * Constructor to create the encoder
     * @param m
     */

    public Encoder(DcMotorEx m, boolean invertedOutput) {
        motor = m;
        this.invertedOutput = invertedOutput;

    }
//TODO the resetting bug we need to fix
    public void updatePosition(){
        position = (invertedOutput? -1:1)* motor.getCurrentPosition();
        //ternary operator (if/else condemned) ww
    }


    public double getPosition() { return position - startPosition; }

    /**
     * Get the type of encoder
     * @return type
     */
    /**
     * Reset the encoder
     */
    public void hardReset(){
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        startPosition = 0;
        position = 0;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        updatePosition();
    }

    public void softReset(){
        updatePosition();
        startPosition = position;
    }

    /**
     * Normal a separate encoder module and motor is a motor encoder
     */

    /**
     * Gets the motor named from the encoderName
     * @param encoderName
     * @return
     */

}
