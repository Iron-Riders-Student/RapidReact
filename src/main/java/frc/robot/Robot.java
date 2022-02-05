package frc.robot;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
    private Servo servo;

    @Override
    public void robotInit() {
        servo = new Servo(0);
        SmartDashboard.putNumber("servo", 0.5);
    }

    @Override
    public void teleopPeriodic() {
        servo.set(SmartDashboard.getNumber("servo", 0.5));
    }
}