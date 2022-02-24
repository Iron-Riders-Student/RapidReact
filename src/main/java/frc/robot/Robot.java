package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {
    public GenericHID controller;
    public MecanumDrive mecanumDrive;
    public Vision vision;
    public Shooter shooter;

    @Override
    public void robotInit() {
        shooter = new Shooter(Constants.SHOOTER_PORT_1, Constants.SHOOTER_PORT_2);
        controller = new GenericHID(0);
        vision = new Vision();
        mecanumDrive = new MecanumDrive();
    }

    @Override
    public void teleopPeriodic() {

        // Interested in checking what the mounting angle is. Kind of tells us how
        // accurate our distance is.
        // vision.determineMountingAngle(vision.estimateDistance(), cameraHeight,
        // objectHeight);
        mecanumDrive.updateSpeed(0, vision.distanceAssist(), 0);

        // Once both distance Assist and autoTarget is fixed, check code
        // Combines both of these functions.
        /*
         * double adjustment = vision.distanceAssist();
         * mecanumDrive.updateSpeed(0, adjustment, 0);
         * SmartDashboard.updateValues();
         * double minError = 0.05; // Used only for Alignment and also is a educated
         * guess.
         * 
         * if (vision.isAligned()) {
         * SmartDashboard.putBoolean("Finished Aligning", true);
         * }
         * else {
         * final double[] params = vision.autoTarget();
         * final double maxInput = Math.max(Math.abs(params[0]), Math.abs(params[1]));
         * if (maxInput < minError) {
         * SmartDashboard.putBoolean("Finishing Aligning", true);
         * }
         * }
         */

        // Drive Train Code:
        // mecanumDrive.updateSpeed(controller.getRawAxis(0), controller.getRawAxis(1), controller.getRawAxis(2));
        // controller.getRawAxis(2));
    }
}