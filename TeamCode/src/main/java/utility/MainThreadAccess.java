package utility;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainThreadAccess {
    private AtomicBoolean mainThreadAccess = new AtomicBoolean(true);

    public boolean doesMainThreadHaveAccess(){
        return mainThreadAccess.get();
    }
    public void takeAccessFromMainThread() {
        mainThreadAccess.set(false);
    }
    public void returnAccessToMainThread(){
        mainThreadAccess.set(true);
    }

}
