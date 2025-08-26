package robotparts.electronics;


import android.os.Looper;

import utility.MainThreadAccess;

public abstract class Electronic {

    protected final MainThreadAccess access = new MainThreadAccess();

    public void takeAccessFromMainThread(){ access.takeAccessFromMainThread(); }
    public void returnAccessToMainThread(){ access.returnAccessToMainThread(); }

    public void stop(){}


}
