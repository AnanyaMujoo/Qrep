package teleop.teleutil;

import java.util.function.Supplier;

public class ClickButton extends PressButton{
    public ClickButton(Supplier<Boolean> buttonSupplier, Runnable action) {
        super(buttonSupplier, action);
    }
    public void update() { //called in the while loop (teleop)
        boolean currentState = isPressed();
        if (!getLastButtonState() && currentState) {
            action.run();
        }
        lastButtonState = currentState;
        //currentState so we limit use of button supplier (with isPressed)
    }
}