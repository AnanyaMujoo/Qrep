package chains;

import static global.Common.chainThread;

import java.util.ArrayList;
import java.util.Arrays;

public class Chain{
    private final ArrayList<Stage> stages;

    public Chain(Stage... stageArray){
        stages = new ArrayList<>();
        stages.addAll(Arrays.asList(stageArray));
    }

    public Chain(ArrayList<Stage> stageArray){
        stages = stageArray;
    }

    public Chain add(Stage stage){
        stages.add(stage);
        return this;
    }

    public ArrayList<Stage> getStages(){
        return stages;
    }
}
