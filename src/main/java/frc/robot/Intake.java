package frc.robot;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.CANCoder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake {
    private TalonSRX intakeMotor;
    private TalonSRX deploymentMotor;
    private CANCoder deploymentEncoder;

    private final double startPosition = 0.0;
    private final double endPosition = 1.0;
    private final double deploymentSpeed = 0.5;

    public Intake(int intakeMotorPort, int deploymentMotorPort) {
        intakeMotor = new TalonSRX(intakeMotorPort);
        deploymentMotor = new TalonSRX(deploymentMotorPort);
        deploymentEncoder = new CANCoder(deploymentMotorPort);
    }

    public void deploy() {
        if (deploymentEncoder.getPosition() < endPosition) {
            deploymentMotor.set(TalonSRXControlMode.Velocity, deploymentSpeed);
        } else {
            deploymentMotor.set(TalonSRXControlMode.Velocity, 0.0);
        }
    }

    public void retract() {
        if (deploymentEncoder.getPosition() > startPosition) {
            deploymentMotor.set(TalonSRXControlMode.Velocity, -deploymentSpeed);
        } else {
            deploymentMotor.set(TalonSRXControlMode.Velocity, 0.0);
        }
    }

    public void updateDashboard() {
        SmartDashboard.putNumber("latch encoder position", deploymentEncoder.getPosition());
    }

    public void intakeBall() {
        intakeMotor.set(TalonSRXControlMode.Velocity, 1);  // TODO: Find good speed
    }

    public void spitOutBall() {
        intakeMotor.set(TalonSRXControlMode.Velocity, -1);  // TODO: Find good speed
    }

    public void stop() {  // Is this really needed?
        intakeMotor.set(TalonSRXControlMode.Velocity, 0);
    }
}
