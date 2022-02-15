package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
    private Vision vision;
    private GenericHID controller;
    private Shooter shooter;

    @Override
    public void robotInit() {
        vision = new Vision();
        controller = new GenericHID(0);
        shooter = new Shooter(Constants.SHOOTER_PORT_1, Constants.SHOOTER_PORT_2);
    }

    @Override
    public void teleopPeriodic() {
        // if(controller.getRawButtonPressed(1)){
        //     mecanumDrive.invertDrive();
        // }
        // mecanumDrive.updateSpeed(controller.getRawAxis(0), controller.getRawAxis(1), controller.getRawAxis(2));
        
        vision.updateDashboard();
        SmartDashboard.putNumber("distance", vision.estimateDistance());
        shooter.updateNumbers();
        shooter.shoot((controller.getRawAxis(1) * 100.0) / 60.0);
    }
}