package utility;

public class InitVar <T> {
    //TODO make it so thst we can take any general onject and make sure the user inilitizes
    private T object;
    private boolean isInitialized = false;

    public void set(T value){
        object = value;
        isInitialized = true;
    }
    public T get(){
        return object;
    }
    public boolean getIsInitialized(){
        return isInitialized;
    }
    public void reset(){
        object = null;
        isInitialized = false;
    }
    //TODO make common interface
}
