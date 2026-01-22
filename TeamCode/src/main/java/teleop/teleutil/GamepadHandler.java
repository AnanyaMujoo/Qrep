package teleop.teleutil;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier; // Used for ReturnCodeSeg replacement
import java.lang.Runnable; // Used for CodeSeg replacement

import global.Common;

public class GamepadHandler implements Common {
    private Gamepad gamepad;
    protected final HashMap<Button, Supplier<Boolean>> pressedMap = new HashMap<Button, Supplier<Boolean>>() {{
        put(Button.A, () -> !gamepad2.get().start && !gamepad1.get().start && gamepad.a);
        put(Button.B, () -> !gamepad2.get().start && !gamepad1.get().start && gamepad.b);
        put(Button.X, () -> gamepad.x);
        put(Button.Y, () -> gamepad.y);
        put(Button.RIGHT_BUMPER, () -> gamepad.right_bumper);
        put(Button.LEFT_BUMPER, () -> gamepad.left_bumper);
        put(Button.DPAD_DOWN, () -> gamepad.dpad_down);
        put(Button.DPAD_UP, () -> gamepad.dpad_up);
        put(Button.DPAD_LEFT, () -> gamepad.dpad_left);
        put(Button.DPAD_RIGHT, () -> gamepad.dpad_right);
        put(Button.LEFT_TRIGGER, () -> gamepad.left_trigger > 0.5);
        put(Button.RIGHT_TRIGGER, () -> gamepad.right_trigger > 0.5);
        put(Button.LEFT_STICK_BUTTON, () -> gamepad.left_stick_button);
        put(Button.RIGHT_STICK_BUTTON, () -> gamepad.right_stick_button);
        put(Button.BACK, () -> gamepad.back);
    }};
    private final ArrayList<ButtonEvent> buttonEventList;
    public double ry, rx, ly, lx, rt, lt;


    public GamepadHandler(Gamepad gp) {
        this.gamepad = gp;
        buttonEventList = new ArrayList<>();
        ry = rx = ly = lx = rt = lt = 0;
    }

    public void addButtonEvent(ButtonEvent event) {
        buttonEventList.add(event);
    }

    public void onPress(Button button, Runnable code) {
        addButtonEvent(new PressButton(pressedMap.get(button), code));
    }

    public void onClick(Button button, Runnable code) {
        addButtonEvent(new ClickButton(pressedMap.get(button), code));
    }

    public void onPressToggle(Button button, Runnable code) {
        addButtonEvent(new ToggleButton(pressedMap.get(button), code));
    }

    public void onClickToggle(Button button, Runnable code1, Runnable code2) {
        addButtonEvent(new ClickToggleButton(pressedMap.get(button), code1, code2));
    }

    public void updateButtonEvents() {
        buttonEventList.forEach(ButtonEvent::update);
        updateValues();
    }

    public void updateValues() {
        ry = -gamepad.right_stick_y;
        rx = gamepad.right_stick_x;
        ly = -gamepad.left_stick_y;
        lx = gamepad.left_stick_x;
        rt = gamepad.right_trigger;
        lt = gamepad.left_trigger;
    }

}




