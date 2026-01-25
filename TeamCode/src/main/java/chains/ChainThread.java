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
    public void update() throws Exception {
           currentStage = stageQueue.poll();
           if (currentStage != null) {
               currentStage.runStage();
            }
    }

    public void addChain(Chain chain){
            stageQueue.addAll(chain.getStages());

    }

    public final void cancel(){
        if(currentStage != null) {
            currentStage.cancel();
            stageQueue.clear();
        }
    }

    public Queue<Stage> getStageQueue(){ return stageQueue; }

    public boolean areChainsRunning(){
        return !stageQueue.isEmpty();
    }

}



