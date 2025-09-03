package robotparts.electronics;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class ContinuousServo extends Electronic{

    private final CRServo crservo;

    public ContinuousServo(CRServo crs, DcMotorSimple.Direction dir){
        crservo = crs;
        crservo.setDirection(dir);
        crservo.setPower(0);
    }

    public void setPower(double p){
        if(access.doesCurrentThreadHaveAccess()){
            crservo.setPower(p);
        }
    }

    @Override
    public void stop(){ setPower(0); }

}
