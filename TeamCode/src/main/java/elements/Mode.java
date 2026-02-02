package elements;


import static global.General.fieldSide;
import static global.General.mode;

import utility.codeseg.CodeSeg;

public enum Mode {
    /**
     * Enum to represent which side of the field we are on
     * The directions are from the audience perspective
     * Note that toString will return the side as in left or right
     */
    SCALER("Left"), NO_SCALER("Right"), UNKNOWN("Unknown");

    /**
     * String to represent the side the robot is on
     */
    private final String side;

    /**
     * Constructor to create the enum
     * @param s
     */
    Mode(String s){
        this.side = s;
    }

    /**
     * Gets the side
     * @return side
     */
    public String getSide(){
        return side;
    }

    /**
     * Creates the enum using the string representation
     * @param side
     * @return
     */
    public static Mode create(String side){
        switch (side) {
            case "Left":
                return SCALER;
            case "Right":
                return NO_SCALER;
            case "Middle":
                return UNKNOWN;
            default:
                return null;
        }
    }

    public static boolean isScaled(){ if(mode != null) { return mode.equals(SCALER) || mode.equals(UNKNOWN);}else {return true; } }

    public static void on(CodeSeg blue, CodeSeg red){
        if(isScaled()){blue.run();}else {red.run();};
    }

    @Override
    public String toString() {
        return getSide();
    }
}
