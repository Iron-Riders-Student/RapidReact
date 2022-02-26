package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
    public GenericHID controller;
    public MecanumDrive mecanumDrive;
    public Vision vision;
    public Shooter shooter;

    // Camera Stuff
    public UsbCamera frontCamera;

    @Override
    public void robotInit() {
        shooter = new Shooter(Constants.SHOOTER_PORT_1, Constants.SHOOTER_PORT_2);
        controller = new GenericHID(0);
        vision = new Vision();
        mecanumDrive = new MecanumDrive();
    }

    @Override
    public void teleopPeriodic() {
        TeleopCamera();
 if (Constants.DRIVER_CONTROL) {
             if (controller.getRawButtonPressed(1)) {
                mecanumDrive.invertDrive();
            }
            mecanumDrive.updateSpeed(controller.getRawAxis(0), controller.getRawAxis(1) * controller.getRawAxis(3),
                    controller.getRawAxis(2));
                    mecanumDrive.updateSpeed(controller.getRawAxis(0), controller.getRawAxis(1), controller.getRawAxis(2));
        } else {
            mecanumDrive.updateSpeed(0, vision.distanceAssist(), vision.steeringAssist());
            vision.updateDashboard();
        }
        SmartDashboard.updateValues();
    }

    public void TeleopCamera()
    {
        // start capture from the camera at the front plugged into USB slot 0
        frontCamera = CameraServer.getInstance().startAutomaticCapture(0);
        // set the stream's resolution to 320x240
        frontCamera.setResolution(320, 240);
        // set the stream's frames per second to 15
        frontCamera.setFPS(15);
    }
}
