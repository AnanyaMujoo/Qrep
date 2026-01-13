package utility;

import androidx.collection.ArraySet;

import global.Log;

public class InitVar <T> {
    private T object;
    private boolean isInitialized = false;

    public void set(T value){
        object = value;
        isInitialized = true;
    }
    public T get(){
        if (isInitialized()){
            return object;
        } else {
            Log.error("Tried to use InitVar get without set");
            return null;
        }
    }
    public boolean isInitialized(){
        return isInitialized;
    }

    // might not be necessary
    public void reset(){
        object = null;
        isInitialized = false;
    }
}
