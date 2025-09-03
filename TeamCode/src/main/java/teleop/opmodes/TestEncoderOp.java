package teleop.opmodes;

import static robotparts.RobotConfig.encoderTest;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Locale;

import chains.Chain;
import robotparts.electronics.MotorWithEncoder;
import teleop.Tele;
import teleop.teleutil.Button;

@TeleOp
public class TestEncoderOp extends Tele {

//    private final Chain testChain = new Chain(
//
//    );

    private MotorWithEncoder motor;

    @Override
    public void initTele() {
        motor = encoderTest.motor;
        motor.softResetEncoder();

        gpA.onClick(Button.X, motor::softResetEncoder);
        gpA.onClick(Button.Y, () -> motor.setTarget(20, 0.5));
        gpA.onClick(Button.B, () -> motor.setTarget(40, 0.5));
        gpA.onClick(Button.A, () -> motor.setTarget(0, 0.5));
        gpA.onClick(Button.RIGHT_BUMPER, () -> motor.adjustTarget(5, 0.2));
        gpA.onClick(Button.LEFT_BUMPER, () -> motor.adjustTarget(-5, 0.2));
    }

    @Override
    public void loopTele() {
//        motor.setPower(0.5*gpA.ry);
        display("Motor Position", String.format(Locale.US, "%.2f cm", motor.getPosition()));
    }
}
