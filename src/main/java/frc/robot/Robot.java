package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
        SmartDashboard.putNumber("speed", shooter.bottomMotor.getEncoder().getVelocity());
    
      mecanumDrive.updateSpeed(0, vision.distanceAssist(), 0);
       SmartDashboard.updateValues();
      // if (controller.getRawButtonPressed(1)) {
           // mecanumDrive.updateSpeed(controller.getRawAxis(0), controller.getRawAxis(1), controller.getRawAxis(2));
        //}
     }
  }