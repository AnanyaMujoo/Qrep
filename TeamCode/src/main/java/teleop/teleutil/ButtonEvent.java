package teleop.teleutil;

import java.util.function.Supplier;

public abstract class ButtonEvent {
    private boolean lastButtonState;
    private Supplier<Boolean> buttonSupplier;

    public ButtonEvent(Supplier<Boolean> buttonSupplier) {
        this.buttonSupplier = buttonSupplier;
        lastButtonState = false;
    }

    public abstract void update();

    protected void updateLastButtonState() {
        lastButtonState = buttonSupplier.get();
    }
    protected boolean isPressed() {
        return buttonSupplier.get();
    }
}