package chains;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class Stage {
    private final Runnable setup;
    private final Runnable loop;
    private final Supplier<Boolean> exitCondition;
    /**Runs unless cancelled*/
    private final Runnable wrapUp;
    /**Runs regardless of cancel, use safe code*/
    private final Runnable alwaysRunsAtEnd;

    private final AtomicBoolean cancelRequested;
    public Stage(Runnable setup, Runnable loop, Supplier<Boolean> exitCondition, Runnable wrapUp, Runnable alwaysRunsAtEnd) {
        this.setup = setup;
        this.loop = loop;
        this.wrapUp = wrapUp;
        this.exitCondition = exitCondition;
        this.alwaysRunsAtEnd = alwaysRunsAtEnd;
        cancelRequested = new AtomicBoolean(false);
    }

    public void runStage(){
        cancelRequested.set(false);
        setup.run();
        while(!exitCondition.get() && !cancelRequested.get()){
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

}
