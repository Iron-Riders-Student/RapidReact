package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class MecanumDrive {
    private boolean inverted;
    private CANSparkMax[] motors;

    public MecanumDrive() {
        this.motors = new CANSparkMax[4];
        this.motors[0] = new CANSparkMax(Constants.WHEEL_PORT_FRONT_LEFT, MotorType.kBrushless);
        this.motors[1] = new CANSparkMax(Constants.WHEEL_PORT_FRONT_RIGHT, MotorType.kBrushless);
        this.motors[2] = new CANSparkMax(Constants.WHEEL_PORT_REAR_LEFT, MotorType.kBrushless);
        this.motors[3] = new CANSparkMax(Constants.WHEEL_PORT_REAR_RIGHT, MotorType.kBrushless);

        this.motors[0].setInverted(true);
        this.motors[1].setInverted(false);
        this.motors[2].setInverted(true);
        this.motors[3].setInverted(false);
        inverted = false;
    }

    public void invertDrive() {
        inverted = !inverted;
    }

    public void updateSpeed(double strafe, double drive, double turn) {
        updateSpeedInternal(strafe, drive, turn, true);
    }

    public void updateAutoSpeed(double strafe, double drive, double turn) {
        updateSpeedInternal(strafe, drive, turn, false);
    }

    private void updateSpeedInternal(double strafe, double drive, double turn, boolean useInverted) {
        double[] speeds = new double[4];
        if (useInverted && inverted) {
            speeds[0] = 0 + strafe - drive + turn;
            speeds[1] = 0 - strafe - drive - turn;
            speeds[2] = 0 - strafe - drive + turn;
            speeds[3] = 0 + strafe - drive - turn;
        } else {
            speeds[0] = 0 - strafe + drive + turn;
            speeds[1] = 0 + strafe + drive - turn;
            speeds[2] = 0 + strafe + drive + turn;
            speeds[3] = 0 - strafe + drive - turn;
        }
        for (int i = 0; i < 4; ++i) {
            speeds[i] *= Constants.DRIVE_SPEED_MULT;
        }
        speeds = normalize(speeds);
        for (int i = 0; i < 4; ++i) {
            this.motors[i].set(speeds[i]);
        }
    }

    // Checks if any speed is greater than 1 and if so reduces all speeds
    private double[] normalize(final double[] vector) {
        double max = 0.0;
        for (int i = 0; i < vector.length; ++i) {
            max = Math.max(max, Math.abs(vector[i]));
        }
        if (max <= 1) {
            return vector;
        }
        double[] normalized = new double[vector.length];
        for (int i = 0; i < vector.length; ++i) {
            normalized[i] = vector[i] / max;
        }
        return normalized;
    }
}