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

    boolean shooterRunning = false;

    @Override
    public void robotInit() {
        shooter = new Shooter();
        controller = new GenericHID(0);
        vision = new Vision();
        mecanumDrive = new MecanumDrive();
        intake = new Intake();
        indexer = new BallIndexer();
    }

    @Override
    public void autonomousPeriodic() {
        if (Timer.getMatchTime() > (15.0 - 3.0)) {
            intake.startDeployment();
            mecanumDrive.updateAutoSpeed(0.0, Constants.DRIVE_SPEED_AUTO, 0.0);
        } else if (Timer.getMatchTime() > (15.0 - 5.0)) {
            mecanumDrive.updateAutoSpeed(0.0, 0.0, vision.steeringAssist());
            shooter.shoot(Shooter.distanceToRPM(vision.estimateDistance()));
        } else if (Timer.getMatchTime() > (15.0 - 8.0)) {
            indexer.extend();
            shooter.shoot(Shooter.distanceToRPM(vision.estimateDistance()));
            intake.finishDeployment();
        } else {
            indexer.retract();
            shooter.stop();
        }
    }

    @Override
    public void teleopPeriodic() {
        if (controller.getRawButtonPressed(5)) {
            intake.intakeBall();
        } else if (controller.getRawButtonPressed(6)) {
            intake.spitOutBall();
        } else if (controller.getRawButtonPressed(10)) {
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
                shooter.shoot(Shooter.distanceToRPM(vision.estimateDistance()));
                shooterRunning = true;
            }
        }

        if (controller.getRawButton(13) && controller.getRawButton(12)) {
            mecanumDrive.updateAutoSpeed(0.0, vision.distanceAssist(), vision.steeringAssist());
        } else if (controller.getRawButton(13)) {
            mecanumDrive.updateAutoSpeed(0.0, 0.0, vision.steeringAssist());
        } else if (controller.getRawButton(12)) {
            mecanumDrive.updateAutoSpeed(0.0, vision.distanceAssist(), 0.0);
        } else {
            double slider = controller.getRawAxis(3) * 0.5 + 0.5;
            mecanumDrive.updateSpeed(controller.getRawAxis(0) * slider, controller.getRawAxis(1) * slider,
                    controller.getRawAxis(2) * slider);
        }

        if (controller.getRawButtonPressed(3)) {
            mecanumDrive.invertDrive();
        }

        vision.updateDashboard();
    }
}
