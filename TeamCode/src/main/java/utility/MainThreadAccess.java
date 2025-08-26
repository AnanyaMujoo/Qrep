package utility;

import android.os.Looper;

import java.util.concurrent.atomic.AtomicBoolean;

import global.Common;

public class MainThreadAccess implements Common {
    private final AtomicBoolean mainThreadAccess = new AtomicBoolean(true);

    public boolean doesCurrentThreadHaveAccess(){
        return isRunningFromMainThread() == mainThreadAccess.get();
    }
    public void takeAccessFromMainThread() {
        mainThreadAccess.set(false);
    }
    public void returnAccessToMainThread(){ mainThreadAccess.set(true); }
    public static boolean isRunningFromMainThread(){ return Thread.currentThread() == mainThread.get(); }

}
