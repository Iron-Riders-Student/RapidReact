package frc.robot;


import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import frc.robot.GripPipelineBall;


import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.vision.VisionRunner;
import edu.wpi.first.vision.VisionThread;



public class Robot extends TimedRobot {
    public GenericHID controller;
    public MecanumDrive mecanumDrive;
    public Vision vision;
    public Shooter shooter;
    public UsbCamera camera = CameraServer.startAutomaticCapture();
    private final Object imgLock = new Object();
    public VisionThread visionThread;   
    private double centerX = 0.0;

    @Override
    public void robotInit() {
        camera.setResolution(1920,1080);
        shooter = new Shooter(Constants.SHOOTER_PORT_1, Constants.SHOOTER_PORT_2);
        controller = new GenericHID(0);
        vision = new Vision();
        mecanumDrive = new MecanumDrive();
        visionThread = new VisionThread(camera, new GripPipelineBall(), pipeline -> {
            if (!pipeline.findBlobsOutput().empty()) {
                Rect r = Imgproc.boundingRect(pipeline.findBlobsOutput());
                synchronized (imgLock) {
                    centerX = r.x + (r.width / 2);
                }
            }
        });
        visionThread.start();
    }

    @Override
    public void teleopPeriodic() {
        if (Constants.DRIVER_CONTROL) {
             if (controller.getRawButtonPressed(1)) {
                mecanumDrive.invertDrive();
            }
            double slider = controller.getRawAxis(3) * 0.5 + 0.5;
            mecanumDrive.updateSpeed(controller.getRawAxis(0), controller.getRawAxis(1) * slider, controller.getRawAxis(2));
        } else {
            mecanumDrive.updateSpeed(0, vision.distanceAssist(), vision.steeringAssist());
            vision.updateDashboard();
        }
        SmartDashboard.updateValues();
    }
}
