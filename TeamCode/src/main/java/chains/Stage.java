package chains;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import utility.Timer;

public class Stage {
    private final Runnable setup;
    private final Runnable loop;
    public final Supplier<Boolean> loopCondition;
    public final Timer timer;
//    /**Runs unless cancelled*/
//    private final Runnable wrapUp;
    /**Runs regardless of cancel, use safe code*/
    private final Runnable alwaysRunsAtEnd;

    public final AtomicBoolean cancelRequested;
//    public Stage(Runnable setup, Supplier<Boolean> loopCondition, Runnable loop, Runnable wrapUp, Runnable alwaysRunsAtEnd) {
//        this.setup = setup;
//        this.loop = loop;
//        this.wrapUp = wrapUp;
//        this.loopCondition = loopCondition;
//        this.alwaysRunsAtEnd = alwaysRunsAtEnd;
//        cancelRequested = new AtomicBoolean(false);
//    }

    public Stage(Runnable setup, Supplier<Boolean> loopCondition, Runnable loop, Runnable alwaysRunsAtEnd) {
        timer = new Timer();
        this.setup = setup;
        this.loop = loop;
        this.loopCondition = loopCondition;
        this.alwaysRunsAtEnd = alwaysRunsAtEnd;
        cancelRequested = new AtomicBoolean(false);
    }

    public Stage(Runnable setup, double seconds, Runnable loop, Runnable alwaysRunsAtEnd){
        timer = new Timer();
        this.setup = setup;
        this.loop = loop;
        this.loopCondition = () -> timer.seconds() < seconds;
        this.alwaysRunsAtEnd = alwaysRunsAtEnd;
        cancelRequested = new AtomicBoolean(false);
    }

    public void runStage(){
        cancelRequested.set(false);
        setup.run();
        timer.reset();
        while(loopCondition.get() && !cancelRequested.get()){
            loop.run();
        }
//        if(!cancelRequested.get()){
//            wrapUp.run();
//        }
        alwaysRunsAtEnd.run();
    }
    public void cancel(){
        cancelRequested.set(true);
    }

}
