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
        SmartDashboard.putNumber("P", pidValues[0]);
        pidController.setTolerance(SmartDashboard.getNumber("AutoAlign: Tolerance", 0.01));
       // SmartDashboard.putNumber("KP_Dist", Constants.KP_DIST);
        //SmartDashboard.putNumber("Target_Dist", Constants.TARGET_DIST);
        //SmartDashboard.putNumber("DIST_MAX_SPEED", Constants.DIST_MAX_SPEED);
        //SmartDashboard.putNumber("okDistance", Constants.okDistance);
       // SmartDashboard.putNumber("Turn_MIN_ANGLE", 1);
        //SmartDashboard.putNumber("Turn_MAX_Speed", 1);
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
        // final double targetHeight = 102.625;
        final double targetHeight = 29;
        final double cameraHeight = 3.125;
        final double cameraAngleToGround = -2;
        final double degrees = cameraAngleToGround + getYAngleOffset();
        SmartDashboard.putNumber("tyAngle", getYAngleOffset());
        // d = (h2-h1) / tan(a1+a2)
        // https://docs.limelightvision.io/en/latest/cs_estimating_distance.html
        double distance = (targetHeight - cameraHeight) / Math.tan(degrees * Math.PI / 180.0);
        //SmartDashboard.putNumber("DistanceToTarget", distance);
        return distance;
    }

    // Determine the mounting angle of the camera given a vision target and its
    // known distance, height off of the ground, and the height of the camera off of
    // the ground. (Untested)
    public double determineMountingAngle(double distance, double cameraHeight, double objectHeight) {
        // NOTE: ty may be negative.
        double ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0.0);
        double angle = Math.atan((cameraHeight - objectHeight) / distance) - ty;
        SmartDashboard.putNumber("Mounting Angle", angle);
        return angle;
    }

    // Adjusts the distance between a vision target and the robot. Uses basic PID
    // with the ty value from the network table. (Tested)
    public double distanceAssist() {
        //Constants.KP_DIST = SmartDashboard.getNumber("KP", Constants.KP_DIST);
        //Constants.DIST_MAX_SPEED = SmartDashboard.getNumber("DIST_MAX_SPEED", Constants.DIST_MAX_SPEED);
        //Constants.TARGET_DIST = SmartDashboard.getNumber("TARGET_DIST", Constants.TARGET_DIST);
        double Distance_Error = !getHasTargets() ? 0.0 : (estimateDistance() - Constants.TARGET_DIST);
        double adjustment = Distance_Error * Constants.KP_DIST;
        adjustment = Math.min(Constants.DIST_MAX_SPEED, Math.max(-Constants.DIST_MAX_SPEED, adjustment));
        SmartDashboard.putNumber("Distance Adjustment", adjustment);
        //Constants.okDistance = SmartDashboard.getNumber("okDistance", Constants.okDistance);
        if (Math.abs(Distance_Error) < Constants.okDistance) {
            return 0;
        }
        return adjustment;
    }

    // Adjusts the angle facing a vision target. Uses basic PID with the tx value
    // from the network table. (Untested)
    public double steeringAssist() {
        if (!getHasTargets() || Math.abs(getXAngleOffset()) < Constants.TURN_MIN_ANGLE) {
            return 0;
        }
        //Constants.TURN_MAX_SPEED = SmartDashboard.getNumber("TURN_MAX_SPEED",  Constants.TURN_MAX_SPEED);
        //Constants.TURN_MIN_ANGLE = SmartDashboard.getNumber("TURN_MIN_ANGLE", Constants.TURN_MIN_ANGLE);
        pidValues[0] = SmartDashboard.getNumber("P", pidValues[0]);
        pidController.setP(pidValues[0]);
        double adjustment = pidController.calculate(getXAngleOffset());
        adjustment = Math.min(Constants.TURN_MAX_SPEED, Math.max(-Constants.TURN_MAX_SPEED, adjustment));
        SmartDashboard.putNumber("Turning Adjustment", adjustment);
        if (isAligned()) {
            return 0;
        }
        return -adjustment;
    }

    // Checks if aligned (tested)
    public boolean isAligned() {
        return pidController.atSetpoint();
    }

    // Combination of distance assist and steering assist (Untested)
    public double[] autoTarget() {
        double dist_assist = distanceAssist();
        double steer_assist = steeringAssist();
        double[] params = { dist_assist + steer_assist, dist_assist - steer_assist };
        return params;
    }
}
