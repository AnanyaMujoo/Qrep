package utility;


import com.qualcomm.robotcore.util.ElapsedTime;

import global.Log;


public class Timer{

    private final ElapsedTime timer = new ElapsedTime();

    private boolean hasBeenReset = false;

    public void reset(){
        timer.reset();
        hasBeenReset = true;
    }

    public double seconds(){
        if(!hasBeenReset){
            Log.error("Tried to use timer without reset!");
        }
        return timer.seconds();
    }
}
