package frc.robot;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// http://docs.limelightvision.io/en/latest/networktables_api.html
public class Vision {
    public NetworkTable table;
    public PIDController pidController;

    public Vision() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
        table.getEntry("pipeline").setNumber(1);
        pidController = new PIDController(Constants.TURN_P, 0.0, 0.0);
        pidController.setSetpoint(0);
        pidController.setTolerance(Constants.TURN_TOLERANCE);
        updateDashboard();
    }

    public double getXAngleOffset() {
        double tx = table.getEntry("tx").getDouble(0.0);
        return Double.isNaN(tx) ? 0.0 : tx;
    }

    public double getYAngleOffset() {
        double ty = table.getEntry("ty").getDouble(0.0);
        return Double.isNaN(ty) ? 0.0 : ty;
    }

    public boolean getHasTargets() {
        return table.getEntry("tv").getDouble(0.0) == 1.0;
    }

    public void updateDashboard() {
        SmartDashboard.putNumber("limelightX", getXAngleOffset());
        SmartDashboard.putNumber("limelightY", getYAngleOffset());
        SmartDashboard.putBoolean("limelightTargets", getHasTargets());
        SmartDashboard.putNumber("DistanceToTarget", estimateDistance());
    }

    public double estimateDistance() {
        final double degrees = Constants.VISION_CAMERA_ANGLE + getYAngleOffset();
        // https://docs.limelightvision.io/en/latest/cs_estimating_distance.html
        return Constants.VISION_DELTA_HEIGHT / Math.tan(degrees * Math.PI / 180.0);
    }

    // Adjusts the distance between a vision target and the robot using ty and PID
    public double distanceAssist() {
        double Distance_Error = !getHasTargets() ? 0.0 : (estimateDistance() - Constants.TARGET_DIST);
        double adjustment = Distance_Error * Constants.KP_DIST;
        adjustment = Math.min(Constants.DIST_MAX_SPEED, Math.max(-Constants.DIST_MAX_SPEED, adjustment));
        SmartDashboard.putNumber("Distance Adjustment", adjustment);
        return (Math.abs(Distance_Error) < Constants.OK_DISTANCE) ? 0 : adjustment;
    }

    // Adjusts the angle facing a vision target using Limelight tx and PID
    public double steeringAssist() {
        if (!getHasTargets() || Math.abs(getXAngleOffset()) < Constants.TURN_MIN_ANGLE) {
            return 0;
        }
        double adjustment = pidController.calculate(getXAngleOffset());
        adjustment = Math.min(Constants.TURN_MAX_SPEED, Math.max(-Constants.TURN_MAX_SPEED, adjustment));
        SmartDashboard.putNumber("Turning Adjustment", adjustment);
        return pidController.atSetpoint() ? 0 : -adjustment;
    }
}
