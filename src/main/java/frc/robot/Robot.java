package frc.robot;

import java.io.IOException;
import java.nio.file.Path;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
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
    public Climber climber;

    public int intakeState = 0;

    public int startShootingTime = 0; // milliseconds

    @Override
    public void robotInit() {
        frontCamera = CameraServer.startAutomaticCapture();
        frontCamera.setResolution(640, 480);
        frontCamera.setFPS(15);
        shooter = new Shooter();
        controller = new GenericHID(0);
        vision = new Vision();
        mecanumDrive = new MecanumDrive();
        intake = new Intake();
        indexer = new BallIndexer();
        climber = new Climber();
        SmartDashboard.putNumber("joystickDeadband", Constants.DEADBAND);
        SmartDashboard.putNumber("joystickExponent", Constants.EXPONENT);
        SmartDashboard.putBoolean("in shooter range", false);
    }

    public double getClampedRPM() {
        double minimum = Constants.SHOOTER_MINIMUM_SPEED;
        double maximum = Constants.SHOOTER_MAXIMUM_SPEED;
        double aimed = Shooter.distanceToRPM(vision.estimateDistance());
        return Math.min(Math.max(aimed, minimum), maximum);
    }

    @Override
    public void autonomousPeriodic() {
        String trajectoryJSON = "paths/part1.wpilib.json";
        Trajectory trajectory = new Trajectory();
        
           try {
              Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(trajectoryJSON);
              trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
           } catch (IOException ex) {
              DriverStation.reportError("Unable to open trajectory: " + trajectoryJSON, ex.getStackTrace());
           }
    }

    @Override
    public void teleopPeriodic() {
        if (startShootingTime > 0) {
            startShootingTime -= 20;
        }

        double dist = vision.estimateDistance();
        SmartDashboard.putBoolean("in shooter range", (dist < 165 && dist > 87));

        // Intake toggle
        if (controller.getRawButtonPressed(2)) {
            if (intakeState == 1) {
                intakeState = 0;
            } else {
                intakeState = 1;
            }
        }
        if (intakeState == 1) {
            intake.intakeBall();
        } else {
            intake.stop();
        }

        if (controller.getRawButton(11)) {
            intake.spitOutBall();
        }

        // This starts the timer for the ball launching
        if (controller.getRawButtonPressed(1) || controller.getRawButtonPressed(7)) {
            startShootingTime = 1000;
        }

        // Auto turning and shooting when button 1 is held
        if (controller.getRawButton(1)) {
            mecanumDrive.updateAutoSpeed(0, 0, vision.steeringAssist());
            shooter.shoot(getClampedRPM());
            if (startShootingTime <= 0) {
                mecanumDrive.updateAutoSpeed(0, 0, 0);
            }
        } else if (controller.getRawButton(7)) {
            // This is in case we pick up the wrong ball
            shooter.shoot(500);
        } else {
            shooter.stop();
        }

        if (controller.getRawButton(5)
                || (controller.getRawButton(1) || controller.getRawButton(7)) && startShootingTime <= 0) {
            indexer.extend();
        } else {
            indexer.retract();
        }

        if (controller.getRawButton(9)) {
            intake.startDeployment();
        }
        // This will finish the deployment after the driver thinks it is deployed
        if (controller.getRawButtonPressed(10)) {
            intake.finishDeployment();
        }

        if (controller.getRawButtonPressed(3)) {
            mecanumDrive.invertDrive();
        }

        if (controller.getRawButton(8)) {
            // Auto turning without shooting
            mecanumDrive.updateAutoSpeed(0, 0, vision.steeringAssist());
        } else if (!controller.getRawButton(1)) {
            // Normal joystick control
            double slider = 1.0; // 0.5 + controller.getRawAxis(2) * 0.5;
            double strafe = joystickResponse(controller.getRawAxis(0)) * slider;
            double move = joystickResponse(controller.getRawAxis(1)) * slider;
            double turn = joystickResponse(controller.getRawAxis(3)) * slider;
            mecanumDrive.updateSpeed(strafe, move, turn);
        }

        if (controller.getRawButton(12)) {
            climber.raise();
        } else if (controller.getRawButton(4)) {
            climber.lower();
        } else {
            climber.stop();
        }
    }

    // This adds a deadzone and nonlinear response to the joystick axis
    private double joystickResponse(double raw) {
        double deadband = SmartDashboard.getNumber("deadband", Constants.DEADBAND);
        double deadbanded = 0.0;
        if (raw > deadband) {
            deadbanded = (raw - deadband) / (1 - deadband);
        } else if (raw < -deadband) {
            deadbanded = (raw + deadband) / (1 - deadband);
        }
        double exponent = SmartDashboard.getNumber("exponent", Constants.EXPONENT) + 1;
        return Math.pow(Math.abs(deadbanded), exponent) * Math.signum(deadbanded);
    }
}
