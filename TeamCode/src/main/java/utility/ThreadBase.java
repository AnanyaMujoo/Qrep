package utility;

import static global.Common.allThreads;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ThreadBase extends java.lang.Thread {

    private final double updateRate;
    private final AtomicBoolean isRunning;
    private final AtomicBoolean wasExceptionThrown;

    public ThreadBase(double updateRate){
        this.updateRate = updateRate;
        this.isRunning = new AtomicBoolean(true);
        this.wasExceptionThrown = new AtomicBoolean(false);
        allThreads.get().add(this);
    }

    public abstract void update() throws RuntimeException;
    public void stopThread(){
        isRunning.set(false);
    }
    @Override
    public void run() {
        while (isRunning.get()){
            try {
                update();
                sleep((long) (1000.0 / updateRate));
            } catch (RuntimeException | InterruptedException r){
                r.printStackTrace();
                //TODO fix print stack trace (should print to main thread)
                wasExceptionThrown.set(true);
                stopThread();
            }
        }
    }
    public void checkForExceptionAndTellMainThread(){
        if(wasExceptionThrown.get()){
           // fault.warn("Exception thrown from inside thread " + name, Expectation.SURPRISING, Magnitude.CATASTROPHIC);
        }
        //TODO PUT THE FAULT
    }
}
