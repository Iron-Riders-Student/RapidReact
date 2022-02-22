package frc.robot;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// http://docs.limelightvision.io/en/latest/networktables_api.html
public class Vision {
    private NetworkTable table;
    private PIDController pidController;

    public Vision() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
        double[] pidValues = SmartDashboard.getNumberArray("AutoAlign: PID Values", new double[] { 0.00009, 0.00007, 0 });
        pidController = new PIDController(pidValues[0], pidValues[1], pidValues[2]);
        pidController.setTolerance(SmartDashboard.getNumber("AutoAlign: Tolerance", 0.01));
    }

    public double getXAngleOffset() {
        double tx = table.getEntry("tx").getDouble(0.0);
        return Double.isNaN(tx)? 0.0: tx;
    }

    public double getYAngleOffset() {
        double ty = table.getEntry("ty").getDouble(0.0);
        return Double.isNaN(ty)? 0.0: ty;
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
        //final double targetHeight = 102.625;
        final double targetHeight = 4;
        final double cameraHeight = 2.626;
        final double cameraAngleToGround = 0;
        final double degrees = cameraAngleToGround + getYAngleOffset();
        // d = (h2-h1) / tan(a1+a2) -
        // https://docs.limelightvision.io/en/latest/cs_estimating_distance.html
        double distance = (targetHeight - cameraHeight) / Math.tan(degrees * Math.PI / 180.0);
        SmartDashboard.putNumber("DistanceToTarget", distance);
        return distance;
    }

    // Determine the mounting angle of the camera given a vision target and its
    // known distance, height off of the ground, and the height of the camera off of the ground.
    public double determineMountingAngle(double distance, double cameraHeight, double objectHeight) {
        // NOTE: ty may be negative.
        double ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0.0);
        return Math.atan((cameraHeight - objectHeight) / distance) - ty;
    }

    // Adjusts the distance between a vision target and the robot. Uses basic PID
    // with the ty value from the network table.
    public double distanceAssist() {
        double adjustment = !getHasTargets() ? 0.0 : (Constants.TARGET_DIST - estimateDistance()) * Constants.KP_DIST;
        adjustment = Math.min(Constants.DIST_MAX_SPEED, Math.max(-Constants.DIST_MAX_SPEED, adjustment));
        SmartDashboard.putNumber("Distance Adjustment", adjustment);
        return adjustment;
    }

    // Adjusts the angle facing a vision target. Uses basic PID with the tx value
    // from the network table.
    public double steeringAssist() {
        if (!getHasTargets() || Math.abs(getXAngleOffset()) < Constants.TURN_MIN_ANGLE) {
            return 0;
        }
        double adjustment = pidController.calculate(getXAngleOffset());
        SmartDashboard.putNumber("turningAdjustement", adjustment);
        adjustment = Math.min(Constants.TURN_MAX_SPEED, Math.max(-Constants.TURN_MAX_SPEED, adjustment));
        SmartDashboard.putNumber("Turning Adjustment", adjustment);
        return adjustment;
    }
}
