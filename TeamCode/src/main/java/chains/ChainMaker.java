package chains;

import static global.Common.chainThread;

import java.util.function.Supplier;

public interface ChainMaker extends Supplier<Chain>, Runnable {
    @Override
    default void run(){
        chainThread.get().cancel();
        chainThread.get().addChain(get());
    }
}
