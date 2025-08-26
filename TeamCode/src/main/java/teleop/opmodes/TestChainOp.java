package teleop.opmodes;

import static robotparts.RobotConfig.drive;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chains.Chain;
import chains.Stage;
import teleop.Tele;
import teleop.teleutil.Button;
import utility.MainThreadAccess;
import utility.Timer;

@TeleOp
public class TestChainOp extends Tele {

    private final Timer timer = new Timer();

    private final Chain TestChain1 = new Chain(
            new Stage(
                    timer::reset,
                    () -> timer.seconds() < 4,
                    () -> display("Time", String.format("%.2f/4.00", timer.seconds())),
                    () -> display("Chain Done"),
                    () -> display("Chain Stopped")
            )
    );

    private final Chain TestChain2 = new Chain(
            new Stage(
                    () ->{
                        drive.takeAccessFromMainThread();
                        timer.reset();
                    },
                    () -> timer.seconds() < 2,
                    () -> {
                        drive.move(0,0,-0.1);
                        },
                    () -> {},
                    () -> {
                        drive.stopAndReturnAccessToMainThread();
                    }
            )
    );


    @Override
    public void initTele() {
        gpA.onClick(Button.Y, TestChain1);
        gpA.onClick(Button.X, TestChain2);
    }

    @Override
    public void loopTele() {
        display("Click Y to Run", "TestChain1 (simple timer)");
        display("Click X to Run", "TestChain2 (spin other way)");
        drive.move(0, 0, 0.1);
    }
}
