package teleop.teleutil;

import java.util.function.Supplier;

public class PressButton extends ButtonEvent{
    protected Runnable action;
    public PressButton(Supplier<Boolean> buttonSupplier, Runnable action) {
        super(buttonSupplier);
        this.action = action;
    }
    public void update() { //called in the while loop (teleop)
        if (isPressed()) {
            action.run();
        }
    }
}
