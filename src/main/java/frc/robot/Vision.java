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

    // public double estimateDistance() {
    //     todo
    // }
}
