package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {
    private GenericHID controller;
    private Shooter shooter;

    @Override
    public void robotInit() {
        controller = new GenericHID(0);
        shooter = new Shooter(Constants.SHOOTER_PORT_1, Constants.SHOOTER_PORT_2);
    }

    @Override
    public void teleopPeriodic() {
        shooter.updateNumbers();
        // shooter.shoot(0);
        shooter.shoot((controller.getRawAxis(1) * 100.0) / 60.0);
    }
}