package teleop.testopmodes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teleop.Tele;
import teleop.teleutil.Button;

@Disabled
@TeleOp
public class TestButtonOp extends Tele{

    private int count1, count2, count3, count4;

    @Override
    public void initTele() {
        count1 = count2 = count3 = count4 = 0;
        gpA.onPress(Button.A, () -> count1++);
        gpA.onClick(Button.B, () -> count2++);
        gpA.onPressToggle(Button.X, () -> count3++);
        gpA.onClickToggle(Button.Y, () -> count4++, () -> count4--);
    }


    @Override
    public void loopTele() {
        display("A -> Press count1++, count1", count1);
        display("B -> Click count2++, count2", count2);
        display("X -> PressToggle count3++, count3", count3);
        display("Y -> ClickToggle count4++, count4--, count4", count4);
    }


}
