package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision {
    private NetworkTable table;

    public Vision() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
    }

    public double getXAngleOffset() {
        return table.getEntry("tx").getDouble(0.0);
    }

    public double getYAngleOffset() {
        return table.getEntry("ty").getDouble(0.0);
    }

    public boolean getHasTargets() {
        return table.getEntry("tv").getDouble(0.0) == 1.0;
    }

    public void updateDashboard() {
        SmartDashboard.putNumber("limelightX", getXAngleOffset());
        SmartDashboard.putNumber("limelightY", getYAngleOffset());
        SmartDashboard.putBoolean("limelightTargets", getHasTargets());
    }

    public double estimateDistance() {
        final double targetHeight = 104; // upper hub is 8ft. 8in./104 in. (~264cm), lower hub is 3ft. 5in./41 in. (~104cm)  - pg 26 of manual
        final double cameraHeight = 2;
        final double cameraAngleToGround = 45;

        // d = (h2-h1) / tan(a1+a2)  - https://docs.limelightvision.io/en/latest/cs_estimating_distance.html
        return (targetHeight-cameraHeight) / Math.tan(cameraAngleToGround+getYAngleOffset());
    }
}
