package robotparts.electronics;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class Motor extends Electronic {

    protected final DcMotorEx motor;

    public Motor(DcMotorEx m, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zpb){
        motor = m;
        motor.setDirection(dir);
        motor.setZeroPowerBehavior(zpb);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(0);
    }

    public void setPower(double p){
        if(access.doesCurrentThreadHaveAccess()){
            motor.setPower(p);
        }
    }

    @Override
    public void stop(){ setPower(0); }

}
