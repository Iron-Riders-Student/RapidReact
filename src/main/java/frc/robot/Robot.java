package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends TimedRobot {
    public GenericHID controller;
    public MecanumDrive mecanumDrive;
    public Vision vision;
    public Shooter shooter;
    public Intake intake;
    public BallIndexer indexer;

    public int intakeState = 0;
    public boolean shooterRunning = false;

    @Override
    public void robotInit() {
        shooter = new Shooter();
        controller = new GenericHID(0);
        vision = new Vision();
        mecanumDrive = new MecanumDrive();
        intake = new Intake();
        indexer = new BallIndexer();
    }

    public double getClampedRPM() {
        double minimum = 500;
        double maximum = 2500;
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

        if (timeFromAutoStart < 5.0) {
            // wait for the robot to move
        } else if (timeFromAutoStart < 7.0) {
            intake.startDeployment();
        } else {
            intake.finishDeployment();
        }

        if (timeFromAutoStart < 5.0) {
            // wait for the robot to move
        } else if (timeFromAutoStart < 10.0) {
            shooter.shoot(getClampedRPM());
        } else {
            shooter.stop();
        }

        if (timeFromAutoStart < 7.0) {
            // wait for the shooter to get up to speed
        } else if (timeFromAutoStart > 10.0) {
            indexer.extend();
        } else {
            indexer.retract();
        }
    }

    @Override
    public void teleopPeriodic() {
        if (controller.getRawButtonPressed(5)) {
            intakeState = 1;
        } else if (controller.getRawButtonPressed(6)) {
            intakeState = 2;
        } else if (controller.getRawButtonPressed(10)) {
            intakeState = 0;
        }
        if (intakeState == 1) {
            intake.intakeBall();
        } else if (intakeState == 2) {
            intake.spitOutBall();
        } else {
            intake.stop();
        }

        if (controller.getRawButtonPressed(1)) {
            indexer.extend();
        }
        if (controller.getRawButtonReleased(1)) {
            indexer.retract();
        }

        if (controller.getRawButtonPressed(2)) {
            if (shooterRunning) {
                shooter.stop();
                shooterRunning = false;
            } else {
                shooter.shoot(getClampedRPM());
                shooterRunning = true;
            }
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

        double strafe = 0.0;
        double move = vision.distanceAssist();
        double turn = vision.steeringAssist();
        if (controller.getRawButton(13) && controller.getRawButton(12)) {
            mecanumDrive.updateAutoSpeed(0, move, turn);
        } else if (controller.getRawButton(13)) {
            mecanumDrive.updateAutoSpeed(0, 0, turn);
        } else if (controller.getRawButton(12)) {
            mecanumDrive.updateAutoSpeed(0, move, 0);
        } else {
            double slider = controller.getRawAxis(3) * 0.5 + 0.5;
            strafe = controller.getRawAxis(0) * slider;
            move = controller.getRawAxis(1) * slider;
            turn = controller.getRawAxis(2) * slider;
            mecanumDrive.updateSpeed(strafe, move, turn);
        }
    }
}
