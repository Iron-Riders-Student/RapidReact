package frc.robot;

import com.revrobotics.CANSparkMax; // SparkMax motor controller, not a SparkMax motor
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BallIndexer {
    private static final int latchMotorChannel = 0;
    private static final double speed = 1.0;
    private CANSparkMax motor;
    private RelativeEncoder encoder;

    private double startPosition = 0.0; // todo: test to determine value
    private double endPosition = 1.0; // todo: test to determine value

    public BallIndexer() {
        motor = new CANSparkMax(latchMotorChannel, MotorType.kBrushless);
        motor.setIdleMode(IdleMode.kBrake);
        encoder = motor.getEncoder();
    }

    public void updateDashboard() {
        SmartDashboard.putNumber("latch encoder position", encoder.getPosition());
    }

    public void releaseBall() {
        if (encoder.getPosition() < endPosition) {
            motor.set(speed);
        } else {
            motor.set(0.0);
        }
    }

    public void acceptNextBall() {
        if (encoder.getPosition() > startPosition) {
            motor.set(-speed);
        } else {
            motor.set(0.0);
        }
    }
}