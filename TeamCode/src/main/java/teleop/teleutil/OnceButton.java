package teleop.teleutil;

import java.util.function.Supplier;

public class OnceButton extends PressButton{
    public OnceButton(Supplier<Boolean> buttonSupplier, Runnable action) {
        super(buttonSupplier, action);
    }
}
//TODO FINISH ONCE BUTTON AND TOGGLE
