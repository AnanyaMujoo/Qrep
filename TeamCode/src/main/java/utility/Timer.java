package utility;


import com.qualcomm.robotcore.util.ElapsedTime;


public class Timer {

    private final ElapsedTime timer = new ElapsedTime();

    private boolean hasBeenReset = false;

    public void reset(){
        timer.reset();
        hasBeenReset = true;
    }

    public double seconds(){
//        fault.warn("Used timer before reset", Expectation.SURPRISING, Magnitude.CRITICAL, hasBeenReset, true);
        // TODO FAULT HERE
        if(!hasBeenReset){
            throw new RuntimeException("bro tried to use the timer without reset");
        }
        return timer.seconds();
    }
}
