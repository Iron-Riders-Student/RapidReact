// Code from Github (MecanumBot/src/main/java/frc/robot/Robot.java) (1/12/2022):
package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {
    private GenericHID controller;
    private MecanumDrive mecanumDrive;

    @Override
    public void robotInit() {
        controller = new GenericHID(0);
        mecanumDrive = new MecanumDrive();
    }

    @Override
    public void teleopPeriodic() {
        if(controller.getRawButtonPressed(1)){
            mecanumDrive.invertDrive();
        }
        mecanumDrive.updateSpeed(controller.getRawAxis(0), controller.getRawAxis(1), controller.getRawAxis(2));
        
    }
}