package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake {
    private CANSparkMax intakeMotor;
    private CANSparkMax deploymentMotor;
    private RelativeEncoder deploymentEncoder;

    private final double startPosition = 0.0;
    private final double endPosition = 1.0;
    private final double deploymentSpeed = 0.5;

    public Intake(int intakeMotorPort, int deploymentMotorPort) {
        intakeMotor = new CANSparkMax(intakeMotorPort, MotorType.kBrushless);
        deploymentMotor = new CANSparkMax(deploymentMotorPort, MotorType.kBrushless);
        deploymentEncoder = deploymentMotor.getEncoder();
    }

    public void deploy() {
        if (deploymentEncoder.getPosition() < endPosition) {
            deploymentMotor.set(deploymentSpeed);
        } else {
            deploymentMotor.set(0.0);
        }
    }

    public void retract() {
        if (deploymentEncoder.getPosition() > startPosition) {
            deploymentMotor.set(-deploymentSpeed);
        } else {
            deploymentMotor.set(0.0);
        }
    }

    public void updateDashboard() {
        SmartDashboard.putNumber("latch encoder position", deploymentEncoder.getPosition());
    }

    public void intakeBall() {
        intakeMotor.set(1);  // TODO: Find good speed
    }

    public void spitOutBall() {
        intakeMotor.set(-1);  // TODO: Find good speed
    }

    public void stop() {  // Is this really needed?
        intakeMotor.set(0);
    }
}
