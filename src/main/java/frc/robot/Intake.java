package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake {
    private TalonSRX intakeMotor;
    private TalonSRX deploymentMotor;

    public Intake() {
        TalonSRXConfiguration intakeConfig = new TalonSRXConfiguration();
        intakeMotor = new TalonSRX(Constants.INTAKE_BELT_PORT);
        intakeMotor.configAllSettings(intakeConfig);
        intakeMotor.setNeutralMode(NeutralMode.Coast);
        TalonSRXConfiguration deploymentConfig = new TalonSRXConfiguration();
        deploymentConfig.forwardSoftLimitEnable = false;
        deploymentConfig.forwardSoftLimitThreshold = Constants.DEPLOY_FORWARD_LIMIT;
        deploymentConfig.reverseSoftLimitEnable = false;
        deploymentConfig.reverseSoftLimitThreshold = Constants.DEPLOY_REVERSE_LIMIT;
        deploymentConfig.peakCurrentLimit = Constants.DEPLOY_CURRENT_PEAK_LIMIT;
        deploymentConfig.peakCurrentDuration = Constants.DEPLOY_CURRENT_PEAK_TIME;
        deploymentConfig.continuousCurrentLimit = Constants.DEPLOY_CURRENT_CONT_LIMIT;
        deploymentMotor = new TalonSRX(Constants.INTAKE_DEPLOYMENT_PORT);
        deploymentMotor.configAllSettings(deploymentConfig);
        deploymentMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        deploymentMotor.setSensorPhase(false);
        deploymentMotor.setNeutralMode(NeutralMode.Brake);
        SmartDashboard.putNumber("duration", Constants.DEPLOY_CURRENT_PEAK_TIME);
        SmartDashboard.putNumber("peakCurrent", Constants.DEPLOY_CURRENT_PEAK_LIMIT);
        SmartDashboard.putNumber("continuousCurrent", Constants.DEPLOY_CURRENT_CONT_LIMIT);
        SmartDashboard.putNumber("percentOutput", 0.0);
        SmartDashboard.putNumber("encoder", 0.0);
        SmartDashboard.putNumber("current", 0.0);
    }

    public void startDeployment() {
        deploymentMotor.configPeakCurrentDuration((int) SmartDashboard.getNumber("duration", Constants.DEPLOY_CURRENT_PEAK_TIME));
        deploymentMotor.configPeakCurrentLimit((int) SmartDashboard.getNumber("peakCurrent", Constants.DEPLOY_CURRENT_PEAK_LIMIT));
        deploymentMotor.configContinuousCurrentLimit((int) SmartDashboard.getNumber("continuousCurrent", Constants.DEPLOY_CURRENT_CONT_LIMIT));
        deploymentMotor.set(ControlMode.PercentOutput, SmartDashboard.getNumber("percentOutput", Constants.DEPLOY_SPEED));
        SmartDashboard.putNumber("encoder", deploymentMotor.getSelectedSensorPosition());
        SmartDashboard.putNumber("current", deploymentMotor.getSupplyCurrent());
    }

    public void finishDeployment() {
        deploymentMotor.set(ControlMode.PercentOutput, 0.0);
        deploymentMotor.setNeutralMode(NeutralMode.Coast);
    }

    public void intakeBall() {
        intakeMotor.set(TalonSRXControlMode.PercentOutput, Constants.INTAKE_SPEED);
    }

    public void spitOutBall() {
        intakeMotor.set(TalonSRXControlMode.PercentOutput, -Constants.INTAKE_SPEED);
    }

    public void stop() {
        intakeMotor.set(TalonSRXControlMode.PercentOutput, 0);
    }
}
