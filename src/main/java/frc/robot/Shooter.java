package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Shooter {
    public CANSparkMax bottomMotor, topMotor;
    public SparkMaxPIDController topPID, bottomPID;

    public Shooter() {
        bottomMotor = new CANSparkMax(Constants.SHOOTER_PORT_BOTTOM, MotorType.kBrushless);
        topMotor = new CANSparkMax(Constants.SHOOTER_PORT_TOP, MotorType.kBrushless);
        bottomMotor.setIdleMode(IdleMode.kCoast);
        topMotor.setIdleMode(IdleMode.kCoast);
        topPID = topMotor.getPIDController();
        bottomPID = bottomMotor.getPIDController();
        topMotor.setInverted(true);
        bottomMotor.setInverted(true);
        topPID.setOutputRange(-1, 1);
        bottomPID.setOutputRange(-1, 1);
        bottomPID.setP(Constants.SHOOTER_P);
        bottomPID.setI(Constants.SHOOTER_I);
        bottomPID.setD(Constants.SHOOTER_D);
        topPID.setP(Constants.SHOOTER_P);
        topPID.setI(Constants.SHOOTER_I);
        topPID.setD(Constants.SHOOTER_D);
    }

    public void shoot(double rpm) {
        topPID.setReference(rpm * Constants.SHOOTER_TOP_MOTOR_CHANGE, ControlType.kVelocity);
        bottomPID.setReference(rpm, ControlType.kVelocity);
    }

    public void stop() {
        topPID.setReference(0, ControlType.kVelocity);
        bottomPID.setReference(0, ControlType.kVelocity);
    }

    public static double distanceToRPM(double distance) {
       // return 1330 + 1.7 * distance + 0.0203 * distance * distance;
    
       // This data is when the ball has a psi of 3.3
       return 4938 + ((-56.7) * distance) + (0.266 * distance * distance);
    }
}
