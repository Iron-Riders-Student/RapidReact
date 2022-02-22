package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {
    public MecanumDrive mecanumDrive;
    public Vision vision;

    @Override
    public void robotInit() {
        vision = new Vision();
        mecanumDrive = new MecanumDrive();
    }

    @Override
    public void teleopPeriodic() {
        mecanumDrive.updateSpeed(0.0, vision.distanceAssist(), 0.0);
        vision.updateDashboard();
     }
  }