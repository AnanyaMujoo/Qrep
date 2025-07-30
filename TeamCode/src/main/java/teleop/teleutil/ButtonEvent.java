package teleop.teleutil;

import java.util.function.Supplier;

public abstract class ButtonEvent {
    protected boolean lastButtonState;
    protected Supplier<Boolean> buttonSupplier;

    public ButtonEvent(Supplier<Boolean> buttonSupplier) {
        this.buttonSupplier = buttonSupplier;
        lastButtonState = false;
    }

    public abstract void update();

    protected boolean getLastButtonState() {
        return lastButtonState;
    }
    protected boolean isPressed() {
        return buttonSupplier.get();
    }
}