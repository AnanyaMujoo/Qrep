package automodules;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import utility.ThreadBase;

public class AutomoduleThread extends ThreadBase {

    private Stage currentStage = null;
    private final ConcurrentLinkedQueue<Stage> stageQueue;


    public AutomoduleThread(double updateRate) {
        super(updateRate);
        stageQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void update() throws RuntimeException {
           currentStage = stageQueue.poll();
           if (currentStage != null) {
               currentStage.runStage();
        }
    }

    public final void addAutomodule(Automodule autoModule){
            stageQueue.addAll(autoModule.getStages());

    }

    public final void cancel(){
        if(currentStage != null) {
            currentStage.cancel();
            stageQueue.clear();
        }
    }

    public Queue<Stage> getStageQueue(){ return stageQueue; }

}


//TODO FINISH THIS AUTOMDOULE TRHEAD




