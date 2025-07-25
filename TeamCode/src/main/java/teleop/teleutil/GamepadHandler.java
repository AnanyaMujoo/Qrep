package teleop.teleutil;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier; // Used for ReturnCodeSeg replacement
import java.lang.Runnable; // Used for CodeSeg replacement

import global.Common;

public class GamepadHandler implements Common {
    private Gamepad gamepad;

    private boolean isBackPressedForTime = false;

    public final HashMap<Button, Supplier<Boolean>> pressedMap = new HashMap<Button, Supplier<Boolean>>() {{
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

//    public final HashMap<Button, Supplier<Float>> valueMap = new HashMap<Button, Supplier<Float>>() {{
//        put(Button.LEFT_TRIGGER, () -> gamepad.left_trigger);
//        put(Button.RIGHT_TRIGGER, () -> gamepad.right_trigger);
//        put(Button.LEFT_STICK_Y, () -> gamepad.left_stick_y);
//        put(Button.LEFT_STICK_X, () -> gamepad.left_stick_x);
//        put(Button.RIGHT_STICK_Y, () -> gamepad.right_stick_y);
//        put(Button.RIGHT_STICK_X, () -> gamepad.right_stick_x);
//    }};

    //public TreeMap<Button, ButtonHandler> handlerMap = new TreeMap<>();
//list of all ways we want to use button: press and it runs on first press, runs continuesly as you press, runs on double click, runs continuely on press then stops on click again
    public double ry, rx, ly, lx, rt, lt;

    public GamepadHandler(Gamepad gp) {
        this.gamepad = gp;
    }
//TODO finish Button Handler
    public void link(Button b, Runnable code){ link(b, code, Modes.GamepadMode.NORMAL); }
    public void link(Button b, Class<? extends ButtonEventHandler> type, Runnable codeSeg) { Objects.requireNonNull(handlerMap.get(b)).addEvent(type, codeSeg); }
    public void link(Button b, AutoModule list) { link(b, () -> bot.addAutoModule(list), Modes.GamepadMode.NORMAL); }
    public void linka(Button b, Supplier<AutoModule> list) { link(b, () -> bot.addAutoModule(list.get()), Modes.GamepadMode.NORMAL); }

    public void link(Button b, Supplier<Boolean> decisionList){ link(b,  decisionList::get, Modes.GamepadMode.NORMAL); }
    public void link(Button b, Supplier<AutoModule> outputList){ link(b, () -> bot.addAutoModule(outputList.get()), Modes.GamepadMode.NORMAL); }
    public void link(Button b, Independent independent){ link(b, () -> bot.addIndependent(independent), Modes.GamepadMode.NORMAL); }
    public void link(Button b, Machine machine){ link(b, () -> bot.addMachine(machine), Modes.GamepadMode.NORMAL); }
    public void link(Button b, AutoModule list, Modes.GamepadMode mode) { link(b, () -> bot.addAutoModule(list), mode); }
    public void link(Button b, Supplier<Boolean> decisionList, Modes.GamepadMode mode){  link(b, decisionList::get, mode); }
    public void link(Button b, Supplier<AutoModule> outputList, Modes.GamepadMode mode){ link(b, () -> bot.addAutoModule(outputList.get()), mode); }
    public void link(Button b, Independent independent, Modes.GamepadMode mode){ link(b, () -> bot.addIndependent(independent), mode); }
    public void link(Button b, Machine machine, Modes.GamepadMode mode){ link(b, () -> bot.addMachine(machine), mode);}
    public void link(Button b, Runnable codeSeg, Modes.GamepadMode mode) { link(b, OnPressEventHandler.class, codeSeg, mode); }
    public void link(Button b, Supplier<Boolean> condition, AutoModule one, AutoModule two){ link(b, () -> {if(condition.get()){bot.addAutoModule(one);}else{bot.addAutoModule(two);}}); }
    public void link(Button b, Supplier<Boolean> condition, Runnable one, Runnable two){ link(b, () -> {if(condition.get()){one.run();}else{two.run();}});}
    public void link(Button b, Supplier<Boolean> conditionOne, Runnable one, Supplier<Boolean> conditionTwo, Runnable two, Runnable three){ link(b, () -> {if(conditionOne.get()){one.run();}else if(conditionTwo.get()){two.run();}else{three.run();}}); }
    public void link(Button b, Supplier<Boolean> conditionOne, AutoModule one, Supplier<Boolean> conditionTwo, AutoModule two, AutoModule three){ link(b, conditionOne, () -> bot.addAutoModuleWithCancel(one), conditionTwo, () -> bot.addAutoModuleWithCancel(two),() -> bot.addAutoModuleWithCancel(three));}

    public void link(Button b, Runnable onOn, Runnable onOff, Modes.GamepadMode mode){ link(b, OnTurnOnEventHandler.class, onOn, mode); link(b, OnTurnOffEventHandler.class, onOff, mode); }
    public void link(Button b, Runnable onOn, Runnable onOff){ link(b, onOn, onOff, Modes.GamepadMode.NORMAL); }

    public void linkWithCancel(Button b, Supplier<Boolean> condition, AutoModule one, AutoModule two){ link(b, () -> {if(condition.get()){bot.addAutoModuleWithCancel(one);}else{bot.addAutoModuleWithCancel(two);}}); }

    public void link(Button b, Class<? extends ButtonEventHandler> type, Runnable codeSeg, Modes.GamepadMode mode){
        switch (mode){
            case NORMAL: Objects.requireNonNull(handlerMap.get(b)).addEvent(type, codeSeg, () -> !isBackPressedForTime); break;
            case AUTOMATED: Objects.requireNonNull(handlerMap.get(b)).addEvent(type, codeSeg, () -> isBackPressedForTime); break;
        }
    }
    private void updateValues(){
        ry = -gamepad.right_stick_y;
        rx = gamepad.right_stick_x;
        ly = -gamepad.left_stick_y;
        lx = gamepad.left_stick_x;
        rt = gamepad.right_trigger;
        lt = gamepad.left_trigger;
    }

    public boolean isBackPressed(){ return isBackPressedForTime; }

    public void run() {
        updateValues();
        isBackPressedForTime = precision.outputTrueForTime(gamepad.back, 0.3);
        Iterator.forAll(handlerMap.values(), ButtonHandler::run);
    }
}






