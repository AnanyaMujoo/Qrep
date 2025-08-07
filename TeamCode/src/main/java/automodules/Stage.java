package automodules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import robotparts.RobotPart;

public class Stage {

    private final Runnable setup;
    private final Runnable loop;
    private final Runnable end;
    private final Supplier<Boolean> exitCondition;

    public Stage(Runnable setup, Runnable loop, Supplier<Boolean> exitCondition, Runnable end) {
        this.setup = setup;
        this.loop = loop;
        this.exitCondition = exitCondition;
        this.end = end;

    }
    //TODO it should just do a start, then while exit condition loop, and then we do a stop.
    public void start(){
        Iterator.forAll(components, StageComponent::start);
        hasStarted = true;
    }

    public void loop(){
        Iterator.forAll(components, StageComponent::loop);
    }

    public boolean shouldStop(){
        return Iterator.forAllConditionOR(components, StageComponent::shouldStop);
    }

    public void runOnStop(){
        Iterator.forAll(components, StageComponent::runOnStop);
        hasStarted = false;
    }

    public boolean isPause(){
        return isPause;
    }

    private void addDefaults(){
        if(Iterator.forAllCount(components, comp -> comp instanceof Exit) == 0){
            components.add(RobotPart.exitAlways());
        }
    }

    public Stage combine(Stage stage){
        Iterator.forAll(stage.components, components::add);
        return this;
    }

    public Stage combine(StageComponent... stageComponents){
        components.addAll(Arrays.asList(stageComponents));
        return this;
    }

    public Stage attach(Stage stage){
        final ArrayList<StageComponent> oldComponents = new ArrayList<>(this.components);
        final ArrayList<StageComponent> newComponents = new ArrayList<>(stage.components);
        FinalInteger exitCode = new FinalInteger();
        return new Stage(false, false){
            @Override
            public void start() {
                Iterator.forAll(oldComponents, StageComponent::start);
                Iterator.forAll(newComponents, StageComponent::start);
                super.hasStarted = true;
            }
            @Override
            public void loop() {
                switch (exitCode.get()) {
                    case 0:
                        Iterator.forAll(oldComponents, StageComponent::loop);
                        Iterator.forAll(newComponents, StageComponent::loop);
                        boolean oldStop = Iterator.forAllConditionOR(oldComponents, StageComponent::shouldStop);
                        boolean newStop = Iterator.forAllConditionOR(newComponents, StageComponent::shouldStop);
                        if (oldStop && newStop) {
                            Iterator.forAll(oldComponents, StageComponent::runOnStop);
                            Iterator.forAll(newComponents, StageComponent::runOnStop);
                            exitCode.set(3);
                        } else if (oldStop) {
                            Iterator.forAll(oldComponents, StageComponent::runOnStop);
                            exitCode.set(1);
                        } else if (newStop) {
                            Iterator.forAll(newComponents, StageComponent::runOnStop);
                            exitCode.set(2);
                        }
                        break;
                    case 1:
                        Iterator.forAll(newComponents, StageComponent::loop);
                        if (Iterator.forAllConditionOR(newComponents, StageComponent::shouldStop)) {
                            Iterator.forAll(newComponents, StageComponent::runOnStop);
                            exitCode.set(3);
                        }
                        break;
                    case 2:
                        Iterator.forAll(oldComponents, StageComponent::loop);
                        if (Iterator.forAllConditionOR(oldComponents, StageComponent::shouldStop)) {
                            Iterator.forAll(oldComponents, StageComponent::runOnStop);
                            exitCode.set(3);
                        }
                        break;
                }
            }
            @Override
            public boolean shouldStop(){
                return exitCode.equals(3);
            }
            @Override
            public void runOnStop() {
                switch (exitCode.get()){
                    case 0:
                        Iterator.forAll(oldComponents, StageComponent::runOnStop);
                        Iterator.forAll(newComponents, StageComponent::runOnStop);
                        break;
                    case 1:
                        Iterator.forAll(newComponents, StageComponent::runOnStop);
                        break;
                    case 2:
                        Iterator.forAll(oldComponents, StageComponent::runOnStop);
                        break;
                }
                super.hasStarted = false;
                exitCode.set(0);
            }
        };
    }
}
