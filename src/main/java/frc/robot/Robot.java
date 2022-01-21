// Code from Github (MecanumBot/src/main/java/frc/robot/Robot.java) (1/12/2022):
package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends TimedRobot {
    private GenericHID controller;
    private MecanumDrive mecanumDrive;

    @Override
    public void robotInit() {
        controller = new GenericHID(0);
        mecanumDrive = new MecanumDrive();
   }

    @Override
    public void autonomousPeriodic() {
        if (Timer.getMatchTime() > (15.0 - 5.0)) {
            // todo: intake.intakeBall();
            mecanumDrive.updateSpeed(0.0, Constants.kAutoSpeed, 0.0);
        } else {
            // todo: intake.stop();
            mecanumDrive.updateSpeed(0.0, 0.0, 0.0);
        }

        if (Timer.getMatchTime() < (15.0 - 5.0) && Timer.getMatchTime() > (15.0 - 10.0)) {
            // todo: indexer.releaseBall();
            // todo: shooter.shoot();
        } else {
            // todo: indexer.acceptNextBall();
            // todo: shooter.stop();
        }
    }

    @Override
    public void teleopPeriodic() {
        if(controller.getRawButtonPressed(1)){
            mecanumDrive.invertDrive();
        }
        mecanumDrive.updateSpeed(controller.getRawAxis(0), controller.getRawAxis(1), controller.getRawAxis(2));
        
    }
}