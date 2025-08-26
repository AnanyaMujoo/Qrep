package teleop.teleutil;

import java.util.function.Supplier;

public class ToggleButton extends ClickButton {
    protected boolean on;
    public ToggleButton(Supplier<Boolean> buttonSupplier, Runnable action) {
        super(buttonSupplier, action);
        on = false;
    }
    public void update() { //called in the while loop (teleop)
        boolean currentState = isPressed();
        if (!getLastButtonState() && currentState) {
            on = !on; //reverses the proccess
        }
        lastButtonState = currentState;
        if (on) {
            action.run();

        }
    }
    //Runs if it was clicked
}

