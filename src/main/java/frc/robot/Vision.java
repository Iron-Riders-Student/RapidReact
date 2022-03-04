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

    public double estimateDistance() {
        double degrees = Constants.VISION_CAMERA_ANGLE + getYAngleOffset();
        // https://docs.limelightvision.io/en/latest/cs_estimating_distance.html
        double distance = Constants.VISION_DELTA_HEIGHT / Math.tan(degrees * Math.PI / 180.0);
        SmartDashboard.putNumber("estimatedDistance", distance);
        return distance;
    }

    // Adjusts the distance between a vision target and the robot using ty and PID
    public double distanceAssist() {
        double distanceError = estimateDistance() - Constants.TARGET_DIST;
        if (!getHasTargets() || Math.abs(distanceError) < Constants.OK_DISTANCE) {
            SmartDashboard.putNumber("Distance Adjustment", 0);
            return 0;
        }
        double adjustment = distanceError * Constants.KP_DIST;
        adjustment = Math.min(Constants.DIST_MAX_SPEED, Math.max(-Constants.DIST_MAX_SPEED, adjustment));
        SmartDashboard.putNumber("Distance Adjustment", adjustment);
        return adjustment;
    }

    // Adjusts the angle facing a vision target using Limelight tx and PID
    public double steeringAssist() {
        if (!getHasTargets() || Math.abs(getXAngleOffset()) < Constants.TURN_MIN_ANGLE) {
            SmartDashboard.putNumber("Turning Adjustment", 0);
            return 0;
        }
        double adjustment = pidController.calculate(getXAngleOffset());
        adjustment = Math.min(Constants.TURN_MAX_SPEED, Math.max(-Constants.TURN_MAX_SPEED, adjustment));
        adjustment = pidController.atSetpoint() ? 0 : -adjustment;
        SmartDashboard.putNumber("Turning Adjustment", adjustment);
        return adjustment;
    }
}
