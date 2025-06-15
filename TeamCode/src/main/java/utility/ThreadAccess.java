package utility;

import java.util.concurrent.Semaphore;

public class ThreadAccess {
    private final Semaphore semaphore = new Semaphore(1);
    /**Only 1 thread allowed*/


}
