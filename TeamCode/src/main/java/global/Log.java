package global;


import java.util.function.Supplier;

public interface Log extends Common {

    default void display(String message, Object value){
        telemetry.get().addData(message, value);
    }

    default void display(String message, Supplier<Object> valueSupplier){
        telemetry.get().addData(message, valueSupplier::get);
    }

    default void display(String message){
        display(message, "");
    }

    default void updateTelemetry(){
        telemetry.get().update();
    }

    default void displayAndUpdateTelemetry(String message, Object value){
        display(message, value);
        updateTelemetry();
    }
}
