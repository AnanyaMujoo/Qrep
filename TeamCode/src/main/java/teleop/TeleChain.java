package teleop;

import chains.Chain;
import chains.Stage;

public interface TeleChain {
    Chain Test = new Chain(
            new Stage(
                    () -> {},
                    () -> true,
                    () -> {},
                    () -> {},
                    () -> {}
            )
    );
}
//TODO make easier to make