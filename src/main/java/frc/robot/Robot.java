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
    public Intake intake;
    public BallIndexer indexer;
    public UsbCamera frontCamera;

    @Override
    public void robotInit() {
        shooter = new Shooter();
        controller = new GenericHID(0);
        vision = new Vision();
        mecanumDrive = new MecanumDrive();
        intake = new Intake();
        indexer = new BallIndexer();
      
        frontCamera = CameraServer.startAutomaticCapture(Constants.CAMERA_USB_PORT);
        frontCamera.setResolution(320, 240);
        frontCamera.setFPS(15);
   }

    @Override
    public void autonomousPeriodic() {
        if (Timer.getMatchTime() > (15.0 - 3.0)) {
            intake.startDeployment();
            mecanumDrive.updateSpeed(0.0, Constants.DRIVE_SPEED_AUTO, 0.0);
        } else  if (Timer.getMatchTime() > (15.0 - 5.0)) {
            mecanumDrive.updateSpeed(0.0, 0.0, vision.steeringAssist());
            shooter.shoot(Shooter.distanceToRPM(vision.estimateDistance()));
        } else if (Timer.getMatchTime() > (15.0 - 8.0)) {
            indexer.extend();
            shooter.shoot(Shooter.distanceToRPM(vision.estimateDistance()));
        } else {
            indexer.retract();
            shooter.stop();
            intake.finishDeployment();
        }
    }       

    @Override
    public void teleopPeriodic() {
        intake.startDeployment();

        // if (controller.getRawButtonPressed(1)) {
        //     mecanumDrive.invertDrive();
        // }
        // if (controller.getRawButtonPressed(2)) {
        //     intake.intakeBall();
        // } else if (controller.getRawButtonPressed(3)) {
        //     intake.spitOutBall();
        // } else if (controller.getRawButtonPressed(4)) {
        //     intake.stop();
        // }
        // if (controller.getRawButtonPressed(5)) {
        //     indexer.extend();
        // } else if (controller.getRawButtonPressed(6)) {
        //     indexer.retract();
        // }
        // if (controller.getRawButtonPressed(7)) {
        //     shooter.shoot(Shooter.distanceToRPM(vision.estimateDistance()));
        // } else if (controller.getRawButtonPressed(8)) {
        //     shooter.stop();
        // }
        // if (controller.getRawButton(9)) {
        //     mecanumDrive.updateSpeed(0.0, 0.0, vision.steeringAssist());
        // }
        // if (controller.getRawButton(10)) {
        //     mecanumDrive.updateSpeed(0.0, vision.distanceAssist(), 0.0);
        // }
        
        // double slider = controller.getRawAxis(3) * 0.5 + 0.5;
        // mecanumDrive.updateSpeed(controller.getRawAxis(0), controller.getRawAxis(1) * slider, controller.getRawAxis(2));
      
        // vision.updateDashboard();
        // SmartDashboard.updateValues(); // does this do anything?
    }
}
