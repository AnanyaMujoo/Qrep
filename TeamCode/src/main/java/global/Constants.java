package global;

public class Constants {

    private Constants(){}

    public static final int DEFAULT_THREAD_REFRESH_RATE = 60;
    public static final int CHAIN_THREAD_REFRESH_RATE = 60;
    public static final int BACKGROUND_THREAD_REFRESH_RATE = 1000;
    public static final int ODOMETRY_THREAD_REFRESH_RATE = 500;
    public static final int INDEPENDENT_THREAD_REFRESH_RATE = 1000;
    public static final int MINIMUM_REFRESH_RATE = 30;
    public static final double ORBITAL_ENCODER_TICKS_PER_REVOLUTION = 537.6;
    public static final double ODOMETRY_ENCODER_TICKS_PER_REV = 8192;
    public static final double DEFAULT_VOLTAGE = 12.5;
    public static final String VUFORIA_KEY = "ASEyqP7/////AAABmb4cSklfmUjBkxJayTfU3Woug32Gg9HrKpH+MBhQdW6OxZu5Fd+RHsBaSsL42WahxSOgd9FJTo4DVzuJaF9MUSjbE/Vy/MkBzjypT5O320DFwHzD8+RHFfWofe0zqC/sk8zBZCDtbPBGwhVIKPm8vrnOQBExz+Acru9akg3rGnVdhfiD6Qs6vuLPe+PVsR0diewGO93rSWI5mHOm3BNyaTfDru3b2qtCAwRsje8uzDLnus4PCEb7mZWE5NJiEMIsZlPUrNd0AllM1SnXVpUtHBwqmTQNCBvbbLP+glWpXlyWzanGq244GHkBT0YD54OAd84XJvTsykplKAXazA+FozkpPENdQVZd+oN4RvCkvZ0a";
    public static final double INCH_TO_CM = 2.54;
    public static final double VUFORIA_TARGET_HEIGHT_CM = 6*INCH_TO_CM;
}
