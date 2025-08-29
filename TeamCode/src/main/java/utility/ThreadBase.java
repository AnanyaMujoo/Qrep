package utility;

import static global.Common.allThreads;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ThreadBase extends java.lang.Thread {

    private final double updateRate;
    private final AtomicBoolean isRunning;
    private final AtomicReference<Exception> exception;

    public ThreadBase(double updateRate){
        this.updateRate = updateRate;
        this.isRunning = new AtomicBoolean(true);
        exception = new AtomicReference<>();
        allThreads.get().add(this);
    }

    public abstract void update() throws Exception;

    public void stopThread(){
        isRunning.set(false);
    }

    @Override
    public void run() {
        while (isRunning.get()){
            try {
                update();
                sleep((long) (1000.0 / updateRate));
            } catch (Exception e){
                exception.set(e);
                stopThread();
            }
        }
    }

    public void checkForExceptionAndTellMainThread(){
        Exception e = exception.get();
        if(e != null){
            if(e instanceof RuntimeException){
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException("Exception thrown in thread", e);
            }
        }
    }
}
