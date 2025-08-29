package robotparts.electronics;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.List;

public class Encoder extends Electronic {

    private final DcMotorEx motor;
    private double position, startPosition = 0; // ticks
    private final boolean invertedOutput;

    public static void setHubsToBulkRead(HardwareMap hardwareMap){
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule module : allHubs) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }

    public Encoder(DcMotorEx m, boolean invertedOutput) {
        motor = m;
        this.invertedOutput = invertedOutput;
    }

    public void updatePosition(){
        position = motor.getCurrentPosition();
        //ternary operator (if/else condemned) ww
    }

    // TODO FIGURE OUT IN WHAT THREAD AND WHEN THE ENCODERS SHOULD BE UPDATED
    // HAVE TO DO BULK UPDATE, CAN DO IT IN CHAIN THREAD? OR MAYBE NEW THREAD?
    // WHEN TO RESET AS WELL? ALWAYS SOFT AT START OR WHAT?
    // LOOP THROUGH ALL ENCODERS AND BULK READ

    // TODO CHECK SOFT RESET DOESNT ACTUALLY RESET MOVE-TO-TARGET POSISITONS
    // IS MANUALLY ADDING AN OFFSET TO THE TARGET POSITIONS NESSESARY??


    public double getPosition() {
        return (invertedOutput? -1:1)*(position - startPosition);
    }

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

}
