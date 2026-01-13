package robotparts.electronics;

import com.qualcomm.robotcore.hardware.VoltageSensor;
import robotparts.RobotPart;

public class Voltage extends RobotPart {
    // Variable to hold the sensor
    private VoltageSensor voltageSensor;

    @Override
    public void init() {
        // We add .get() to unwrap the InitVar and find the real HardwareMap
        voltageSensor = hardwareMap.get().voltageSensor.iterator().next();
    }
    // Method to read the voltage
    public double getVoltage() {
        if (voltageSensor != null) {
            return voltageSensor.getVoltage();
        }
        return 0.0;
    }
}