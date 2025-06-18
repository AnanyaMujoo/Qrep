package robotparts.electronics;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class MotorWithEncoder extends Motor {
    /**
     * Constructor with parameters
     *
     * @param m
     * @param dir
     * @param zpb
     */
    public MotorWithEncoder(DcMotorEx m, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior zpb) {
        super(m, dir, zpb);
    }
}
