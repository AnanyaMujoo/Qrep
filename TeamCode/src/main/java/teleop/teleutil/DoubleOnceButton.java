package teleop.teleutil;

import java.util.function.Supplier;

public class DoubleOnceButton extends ToggleButton {
    private Runnable action2;
    public DoubleOnceButton(Supplier<Boolean> buttonSupplier, Runnable action1, Runnable action2) {
        super(buttonSupplier, action1);
        this.action2 = action2;
    }
    public void update() { //called in the while loop (teleop)
        boolean currentState = isPressed();
        if (!getLastButtonState() && currentState) {
            on = !on; //reverses the proccess
            if (on) {
                action.run();

            }
            else action2.run();
        }
        lastButtonState = currentState;

    }

}
