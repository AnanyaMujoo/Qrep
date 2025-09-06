package chains;

import static global.Constants.ALWAYS_FALSE;
import static global.Constants.EMPTY_RUNNABLE;


import java.util.Arrays;
import java.util.function.Supplier;

import robotparts.RobotPart;
import utility.Timer;

public class StageBuilder {

    public static Stage stage(RobotPart part, Runnable... runnables){
        return new Stage(() -> { part.takeAccessFromMainThread(); Arrays.stream(runnables).forEach(Runnable::run);  }, ALWAYS_FALSE, EMPTY_RUNNABLE, () -> { part.stop(); part.returnAccessToMainThread();});
    }

    public static Stage stage(Runnable... runnables){
        return new Stage(() -> Arrays.stream(runnables).forEach(Runnable::run), ALWAYS_FALSE, EMPTY_RUNNABLE, EMPTY_RUNNABLE);
    }

    public static Stage stage(Runnable runnable, Supplier<Boolean> condition){
        return new Stage(runnable, condition, EMPTY_RUNNABLE, EMPTY_RUNNABLE);
    }

    public static Stage stage(RobotPart part, Runnable runnable, double seconds){
        return new Stage(() -> { part.takeAccessFromMainThread(); runnable.run();}, seconds, EMPTY_RUNNABLE, () -> { part.stop(); part.returnAccessToMainThread();});
    }

    public static Stage stage(Runnable runnable, double seconds){
        return new Stage(runnable, seconds, EMPTY_RUNNABLE, EMPTY_RUNNABLE);
    }

    public static Stage stage(double seconds){
        return new Stage(EMPTY_RUNNABLE, seconds, EMPTY_RUNNABLE, EMPTY_RUNNABLE);
    }
}
