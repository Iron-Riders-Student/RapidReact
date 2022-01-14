package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Intake {
    private CANSparkMax intakeMotor;

    public Intake(int intakeMotorPort) {
        intakeMotor = new CANSparkMax(intakeMotorPort, MotorType.kBrushless);
    }

    public void intakeBall() {
        intakeMotor.set(2);  // TODO: Find good speed
    }

    public void spitOutBall() {
        intakeMotor.set(-1);  // TODO: Find good speed
    }

    public void stop() {  // Is this really needed?
        intakeMotor.set(0);
    }
}
