package frc.robot;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// http://docs.limelightvision.io/en/latest/networktables_api.html
public class Vision {
    private NetworkTable table;
    private PIDController pidController;
    double[] pidValues;

    public Vision() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
        pidValues = SmartDashboard.getNumberArray("AutoAlign: PID Values", new double[] { 0.1, 0, 0 });
        pidController = new PIDController(pidValues[0], pidValues[1], pidValues[2]);
        pidController.setSetpoint(0);
        pidController.setTolerance(SmartDashboard.getNumber("AutoAlign: Tolerance", 0.01));
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
        final double targetHeight = 29;
        final double cameraHeight = 3.125;
        final double cameraAngleToGround = -2;
        final double degrees = cameraAngleToGround + getYAngleOffset();
        SmartDashboard.putNumber("tyAngle", getYAngleOffset());
        // https://docs.limelightvision.io/en/latest/cs_estimating_distance.html
        double distance = (targetHeight - cameraHeight) / Math.tan(degrees * Math.PI / 180.0);
        return distance;
    }

    // Adjusts the distance between a vision target and the robot using ty and PID
    public double distanceAssist() {
        double Distance_Error = !getHasTargets() ? 0.0 : (estimateDistance() - Constants.TARGET_DIST);
        double adjustment = Distance_Error * Constants.KP_DIST;
        adjustment = Math.min(Constants.DIST_MAX_SPEED, Math.max(-Constants.DIST_MAX_SPEED, adjustment));
        SmartDashboard.putNumber("Distance Adjustment", adjustment);
        return (Math.abs(Distance_Error) < Constants.okDistance) ? 0 : adjustment;
    }

    // Adjusts the angle facing a vision target using Limelight tx and PID
    public double steeringAssist() {
        if (!getHasTargets() || Math.abs(getXAngleOffset()) < Constants.TURN_MIN_ANGLE) {
            return 0;
        }
        pidValues[0] = SmartDashboard.getNumber("P", pidValues[0]);
        pidController.setP(pidValues[0]);
        double adjustment = pidController.calculate(getXAngleOffset());
        adjustment = Math.min(Constants.TURN_MAX_SPEED, Math.max(-Constants.TURN_MAX_SPEED, adjustment));
        SmartDashboard.putNumber("Turning Adjustment", adjustment);
        return isAligned() ? 0 : -adjustment;
    }

    public boolean isAligned() {
        return pidController.atSetpoint();
    }
}
