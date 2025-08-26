package utility;

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
        }
        else {
            //TODO PUT THE FAULT
            throw new RuntimeException("lil bot u didnt set bro tried to get without set");
        }
    }
    public boolean isInitialized(){
        return isInitialized;
    }

    // might not be nessesary
    public void reset(){
        object = null;
        isInitialized = false;
    }
}
