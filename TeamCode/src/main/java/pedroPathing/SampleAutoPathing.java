package pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class SampleAutoPathing extends OpMode {
    private Follower follower;
    private Timer pathTimer, OpModetimer;
    public enum PathState{
        START_POS,
        END_POS



    }

    @Override
    public void init(){

    }
    @Override
    public void loop(){
    }
}
