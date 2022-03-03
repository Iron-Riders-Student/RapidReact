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
        if (controller.getRawButtonPressed(2)) {
            intake.intakeBall();
        } else if (controller.getRawButtonPressed(3)) {
            intake.spitOutBall();
        } else if (controller.getRawButtonPressed(4)) {
            intake.stop();
        }

        if (controller.getRawButtonPressed(5)) {
            indexer.extend();
        }
        if (controller.getRawButtonReleased(5)) {
            indexer.retract();
        }
        
        if (controller.getRawButtonPressed(7)) {
            shooter.shoot(Shooter.distanceToRPM(vision.estimateDistance()));
        } else if (controller.getRawButtonPressed(8)) {
            shooter.stop();
        }

        if (controller.getRawButton(9) && controller.getRawButton(10)) {
            mecanumDrive.updateAutoSpeed(0.0, vision.distanceAssist(), vision.steeringAssist());
        } else if (controller.getRawButton(9)) {
            mecanumDrive.updateAutoSpeed(0.0, 0.0, vision.steeringAssist());
        } else if (controller.getRawButton(10)) {
            mecanumDrive.updateAutoSpeed(0.0, vision.distanceAssist(), 0.0);
        } else {
            double slider = controller.getRawAxis(3) * 0.5 + 0.5;
            mecanumDrive.updateSpeed(controller.getRawAxis(0), controller.getRawAxis(1) * slider,
                    controller.getRawAxis(2));
        }

        if (controller.getRawButtonPressed(1)) {
            mecanumDrive.invertDrive();
        }

        vision.updateDashboard();
    }
}
