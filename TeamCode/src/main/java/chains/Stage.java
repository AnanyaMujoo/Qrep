package chains;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class Stage {
    private final Runnable setup;
    private final Runnable loop;
    private final Supplier<Boolean> loopCondition;
    /**Runs unless cancelled*/
    private final Runnable wrapUp;
    /**Runs regardless of cancel, use safe code*/
    private final Runnable alwaysRunsAtEnd;

    private final AtomicBoolean cancelRequested;
    public Stage(Runnable setup, Supplier<Boolean> loopCondition, Runnable loop, Runnable wrapUp, Runnable alwaysRunsAtEnd) {
        this.setup = setup;
        this.loop = loop;
        this.wrapUp = wrapUp;
        this.loopCondition = loopCondition;
        this.alwaysRunsAtEnd = alwaysRunsAtEnd;
        cancelRequested = new AtomicBoolean(false);
    }

    public void runStage(){
        cancelRequested.set(false);
        setup.run();
        while(loopCondition.get() && !cancelRequested.get()){
            loop.run();
        }
        if(!cancelRequested.get()){
            wrapUp.run();
        }
        alwaysRunsAtEnd.run();
    }
    public void cancel(){
        cancelRequested.set(true);
    }

    // TODO DECIDE REMOVE WRAP UP?
    // Not sure if it will be used at all
    // Can make always run at end take in a boolean which is like exited normally?

}
