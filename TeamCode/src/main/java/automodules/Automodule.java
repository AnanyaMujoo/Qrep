package automodules;

import java.util.ArrayList;
import java.util.Arrays;

import automodules.Stage;

public class Automodule {
    private final ArrayList<Stage> stages;

    public Automodule(Stage... stageArray){
        stages = new ArrayList<>();
        stages.addAll(Arrays.asList(stageArray));
    }
    public ArrayList<Stage> getStages(){
        return stages;
    }
}
