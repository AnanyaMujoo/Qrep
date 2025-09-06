package robotparts.electronics;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Encoder extends Electronic {

    public final DcMotorEx motor;
    private final AtomicInteger position;
    private final AtomicInteger startPosition;
//    private double position, startPosition = 0; // ticks
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
        position = new AtomicInteger(0);
        startPosition = new AtomicInteger(0);
    }

    public void updatePosition(){
        position.set(motor.getCurrentPosition());
    }


    public double getPosition() {
        return (invertedOutput?-1:1)*(position.get() - startPosition.get());
    }

    public void hardReset(){
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        startPosition.set(0);
        position.set(0);
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
        startPosition.set(position.get());
    }

    public int getStartPosition(){
        return startPosition.get();
    }

}
