package chains;

import static global.Common.chainThread;

import java.util.ArrayList;
import java.util.Arrays;

public class Chain implements Runnable{
    private final ArrayList<Stage> stages;

    public Chain(Stage... stageArray){
        stages = new ArrayList<>();
        stages.addAll(Arrays.asList(stageArray));
    }
    public ArrayList<Stage> getStages(){
        return stages;
    }

    @Override
    public void run() {
        chainThread.get().cancel();
        chainThread.get().addChain(this);
    }
}
