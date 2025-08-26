package chains;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import utility.ThreadBase;

public class ChainThread extends ThreadBase {

    private Stage currentStage;
    private final ConcurrentLinkedQueue<Stage> stageQueue;


    public ChainThread(double updateRate) {
        super(updateRate);
        currentStage = null;
        stageQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void update() throws RuntimeException {
           currentStage = stageQueue.poll();
           if (currentStage != null) {
               currentStage.runStage();
            }
    }

    public final void addChain(Chain chain){
            stageQueue.addAll(chain.getStages());

    }

    public final void cancel(){
        if(currentStage != null) {
            currentStage.cancel();
            stageQueue.clear();
        }
    }

    public Queue<Stage> getStageQueue(){ return stageQueue; }

}

//TODO TEST THIS



