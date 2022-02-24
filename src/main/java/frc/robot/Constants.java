package frc.robot;

public class Constants {

    public static final double kSpeedMultiplier = 0.1;

    public static final int SHOOTER_PORT_1 = 5;
    public static final int SHOOTER_PORT_2 = 6;

    // Automatic distancing
    public static final double TARGET_DIST = 5; // Orginally 100
    public static final double KP_DIST = 0.001;
    public static final double DIST_MAX_SPEED = 0.5;

    // Automatic turning
    public static final double TURN_MIN_ANGLE = 1.0;
    public static final double TURN_MAX_SPEED = 0.5;

    // Ports
    public static final int kFrontLeftChannel = 1;
    public static final int kRearLeftChannel = 4;
    public static final int kFrontRightChannel = 3;
    public static final int kRearRightChannel = 2;
}