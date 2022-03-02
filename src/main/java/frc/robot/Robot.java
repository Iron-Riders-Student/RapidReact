package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
    public GenericHID controller;
    public MecanumDrive mecanumDrive;
    public Vision vision;
    public Shooter shooter;
    private Intake intake;
    public UsbCamera frontCamera;

    @Override
    public void robotInit() {
        shooter = new Shooter(Constants.SHOOTER_PORT_1, Constants.SHOOTER_PORT_2);
        controller = new GenericHID(0);
        vision = new Vision();
        mecanumDrive = new MecanumDrive();
        intake = new Intake(1, 2); // TODO: Change port to ports file, add button on controller
      
        // start capture from the camera at the front plugged into USB slot 0
        frontCamera = CameraServer.getInstance().startAutomaticCapture(0);
        // set the stream's resolution to 320x240
        frontCamera.setResolution(320, 240);
        // set the stream's frames per second to 15
        frontCamera.setFPS(15);
   }

    @Override
    public void autonomousPeriodic() {
        if (Timer.getMatchTime() > (15.0 - 5.0)) {
            // intake.intakeBall();?
            mecanumDrive.updateSpeed(0.0, Constants.kAutoSpeed, 0.0);
        } else {
            // intake.stop();?
            mecanumDrive.updateSpeed(0.0, 0.0, 0.0);
        }
        if (Timer.getMatchTime() < (15.0 - 5.0) && Timer.getMatchTime() > (15.0 - 10.0)) {
            // todo: indexer.extend();
            // todo: shooter.shoot();
        } else {
            // todo: indexer.retract();
            // todo: shooter.stop();
        }
    }

    @Override
    public void teleopPeriodic() {
        if (Constants.DRIVER_CONTROL) {
             if (controller.getRawButtonPressed(1)) {
                mecanumDrive.invertDrive();
            } else if (controller.getRawButtonPressed(3)) {
                intake.intakeBall();
            } else if (controller.getRawButtonPressed(2)) {
                intake.spitOutBall();
            }
            double slider = controller.getRawAxis(3) * 0.5 + 0.5;
            mecanumDrive.updateSpeed(controller.getRawAxis(0), controller.getRawAxis(1) * slider, controller.getRawAxis(2));
        } else {
            mecanumDrive.updateSpeed(0, vision.distanceAssist(), vision.steeringAssist());
        }
      
        vision.updateDashboard();
        intake.updateDashboard();
        SmartDashboard.updateValues(); // does this do anything?
    }
}
