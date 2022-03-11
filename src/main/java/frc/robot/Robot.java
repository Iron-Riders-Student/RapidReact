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

    public int intakeState = 0;
    public boolean withLimelight = true;

    public double startShootingTime = 0.0;

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
        SmartDashboard.putNumber("joystickDeadband", Constants.DEADBAND);
        SmartDashboard.putNumber("joystickExponent", Constants.EXPONENT);
    }

    public double getClampedRPM() {
        double minimum = Constants.SHOOTER_MINIMUM_SPEED;
        double maximum = Constants.SHOOTER_MAXIMUM_SPEED;
        double aimed = Shooter.distanceToRPM(vision.estimateDistance());
        return Math.min(Math.max(aimed, minimum), maximum);
    }

    @Override
    public void autonomousPeriodic() {
        double timeFromAutoStart = 15.0 - Timer.getMatchTime();

        if (timeFromAutoStart < 3.0) {
            mecanumDrive.updateAutoSpeed(0.0, Constants.DRIVE_SPEED_AUTO, 0.0);
        } else if (timeFromAutoStart < 5.0) {
            mecanumDrive.updateAutoSpeed(0.0, 0.0, vision.steeringAssist());
        } else {
            mecanumDrive.updateAutoSpeed(0.0, 0.0, 0.0);
        }

        if (timeFromAutoStart < 2.0) {
            intake.startDeployment();
        } else {
            intake.finishDeployment();
            intake.intakeBall();
        }

        if (timeFromAutoStart > 3.0) {
            shooter.shoot(getClampedRPM());
        }

        if (timeFromAutoStart < 6.0) {
            // wait for the shooter to get up to speed
        } else if (timeFromAutoStart < 8.0) {
            indexer.extend();
        } else if (timeFromAutoStart < 10.0) {
            indexer.retract();
        } else if (timeFromAutoStart < 12.0) {
            indexer.extend();
        } else {
            indexer.retract();
        }
    }

    @Override
    public void teleopPeriodic() {
        if (controller.getRawButtonPressed(9)) {
            withLimelight = !withLimelight;
        }
        SmartDashboard.putBoolean("With Limelight", withLimelight);

        if (controller.getRawButtonPressed(4)) {
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

        if (controller.getRawButton(8)) {
            intake.spitOutBall();
        }

        if (controller.getRawButtonPressed(1) || controller.getRawButtonPressed(6)) {
            startShootingTime = Timer.getMatchTime();
        }

        if (controller.getRawButton(1)) {
            mecanumDrive.updateAutoSpeed(0, 0, vision.steeringAssist());
            if (withLimelight) {
                shooter.shoot(getClampedRPM());
            } else {
                shooter.shoot(1800);
            }
            if (startShootingTime - Timer.getMatchTime() > 1.5) {
                indexer.extend();
            }
        } else if (controller.getRawButton(6)) {
            shooter.shoot(500);
            if (startShootingTime - Timer.getMatchTime() > 1.5) {
                indexer.extend();
            }
        } else {
            indexer.retract();
            shooter.stop();
        }

        if (controller.getRawButton(16)) {
            intake.startDeployment();
        }
        if (controller.getRawButtonPressed(15)) {
            intake.finishDeployment();
        }

        if (controller.getRawButtonPressed(3)) {
            mecanumDrive.invertDrive();
        }

        if (controller.getRawButton(7)) {
            indexer.extend();
        } else {
            indexer.retract();
        }

        if (controller.getRawButton(2)) {
            mecanumDrive.updateAutoSpeed(0, 0, vision.steeringAssist());
        } else if (!controller.getRawButton(1)) {
            double slider = 0.5 - controller.getRawAxis(3) * 0.5;
            double strafe = joystickResponse(controller.getRawAxis(0)) * slider;
            double move = joystickResponse(controller.getRawAxis(1)) * slider;
            double turn = joystickResponse(controller.getRawAxis(2)) * slider;
            mecanumDrive.updateSpeed(strafe, move, turn);
        }
    }

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
