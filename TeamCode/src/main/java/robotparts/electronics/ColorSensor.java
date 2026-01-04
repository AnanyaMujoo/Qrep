package robotparts.electronics;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ColorSensor extends Electronic{

    private final NormalizedColorSensor colorSensor;

    public ColorSensor(NormalizedColorSensor colorSensor){
        this.colorSensor = colorSensor;
    }

//    public int getRed(){
//        return colorSensor.red();
//    }
//    public int getGreen(){
//        return colorSensor.green();
//    }
//    public int getBlue(){
//        return colorSensor.blue();
//    }
    public double getDistance() { return ((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM); }
}
