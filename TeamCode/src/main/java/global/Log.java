package global;

//import static robotparts.RobotConfig.encoderTest;

import java.util.AbstractMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public interface Log extends Common {

    ConcurrentLinkedQueue<Map.Entry<String, Supplier<Object>>> telemetryQueue = new ConcurrentLinkedQueue<>();

    default void resetTelemetryQueue() {
        synchronized (telemetryQueue) {
            telemetryQueue.clear();
        }
    }

    default void display(String message, Supplier<Object> valueSupplier) {
        synchronized (telemetryQueue) {
            boolean exists = telemetryQueue.stream().anyMatch(entry -> entry.getKey().equals(message));
            if (!exists) {
                telemetryQueue.add(new AbstractMap.SimpleEntry<>(message, valueSupplier));
            }
        }
    }

    default void display(String message, Object value) {
        display(message, () -> value);
    }

    default void display(String message, String format, Object value) {
        display(message, () -> String.format(Locale.US, format, value));
    }

    default void display(String message) {
        display(message, "");
    }

    default void updateTelemetry() {
        synchronized (telemetryQueue) {
            Map.Entry<String, Supplier<Object>> entry;
            while ((entry = telemetryQueue.poll()) != null) {
                Qtelemetry.get().addData(entry.getKey(), entry.getValue().get());
            }
            Qtelemetry.get().update();
        }
    }

    default void displayAndUpdateTelemetry(String message, Object value) {
        display(message, value);
        updateTelemetry();
    }

    static void error(String message) {
        throw new RuntimeException(" " + message);
    }
}
