package frc.robot;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.PIDSubsystem;

public class Vision {

 /* http://docs.limelightvision.io/en/latest/networktables_api.html
  tv = Whether the limelight has any valid targets (0 or 1)
  tx = Horizontal Offset From Crosshair To Target (-27 degrees to 27 degrees)
  ty = Vertical Offset From Crosshair To Target (-20.5 degrees to 20.5 degrees)
  ta = Target Area (0% of image to 100% of image)
  There are more values we could be using. Check the documentation.
  */

    private NetworkTable table;
    private double tv, tx, ty, ta;
    private double mounting_angle;
    private double prev_tx = 1.0;
    private double steering_factor = 0.25;
    public boolean stopSteer = false;
    private double adjustment = 0.0;
    private double prevHeading = 0;
    private double backlashOffset = 0.0;

    private PIDController pidController;
    private boolean newPIDLoop = false;
    


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
        final double targetHeight = 102.625; // upper hub is 8ft. 8in./104 in. (~264cm), lower hub is 3ft. 5in./41 in. (~104cm)  - pg 26 of manual
        final double cameraHeight = 1.8;
        final double cameraAngleToGround = 40.91;
        final double degrees = cameraAngleToGround+getYAngleOffset();
        // d = (h2-h1) / tan(a1+a2)  - https://docs.limelightvision.io/en/latest/cs_estimating_distance.html
        return (targetHeight-cameraHeight) / Math.tan(degrees * Math.PI / 180.0);
    }

    /* Determine the mounting angle of the camera given a vision target and its known distance, height off of the ground,
   and the height of the camera off of the ground. */
  public void determineMountingAngle(double distance, double cameraHeight, double objectHeight) {
    // NOTE: ty may be negative.
    ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0.0);
    mounting_angle = Math.atan((cameraHeight - objectHeight) / distance) - ty;
  }

  // Adjusts the distance between a vision target and the robot. Uses basic PID with the ty value from the network table.
  public double distanceAssist() {
    tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
    ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0.0);
    SmartDashboard.putNumber("Crosshair Vertical Offset", ty);
    double adjustment = 0.0;
    double area_threshold = 1.75;
    double Kp = 0.225;

    if (tv == 1.0) {
      adjustment = (area_threshold - ta) * Kp;
    }
    adjustment = Math.signum(adjustment) * Math.min(Math.abs(adjustment), 0.5);
    return adjustment;
  }

  // Adjusts the angle facing a vision target. Uses basic PID with the tx value from the network table.
  public double steeringAssist(MecanumDrive dt) {
    tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
    tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0.0);
    ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0.0);
    SmartDashboard.putNumber("Crosshair Horizontal Offset", tx);
    SmartDashboard.putNumber("Found Vision Target", tv);
    SmartDashboard.putNumber("Prev_tx", prev_tx);
    tx = Double.isNaN(tx) ? 0 : tx;
    double[] pidValues = SmartDashboard.getNumberArray("AutoAlign: PID Values", new double[]{0.015, 0, 0});
    pidController.setPID(pidValues[0], pidValues[1], pidValues[2]);
    pidController.setTolerance(SmartDashboard.getNumber("AutoAlign: Tolerance", 0.01));
  
    if (tv == 1.0 && !stopSteer) {
      if (ta > SmartDashboard.getNumber("Area Threshold", 0.02)) {
        adjustment = pidController.calculate(tx);
        prev_tx = tx;
        
        if (!newPIDLoop) {
          newPIDLoop = true;
          pidController.setSetpoint(Math.signum(prev_tx) * SmartDashboard.getNumber("AutoAlign: Backlash Offset", backlashOffset));
        }
      }
    } else {
      newPIDLoop = false;
      pidController.reset();
      adjustment = Math.signum(prev_tx) * steering_factor;
    }

    if (Math.abs(tx) < 1.0 && Math.abs(prev_tx) < 1.0 && Math.abs(((Object) dt).getHeading() - prevHeading) < 1) stopSteer = true;
    else stopSteer = false;
    if(stopSteer) {
      adjustment = 0;
    }
    prevHeading = dt.getHeading();

    SmartDashboard.putBoolean("Stop Auto Steering", stopSteer);

    adjustment = Math.signum(tx) * Math.min(Math.abs(adjustment), 0.5);
    SmartDashboard.putNumber("Adjustment", adjustment);
    return adjustment;
  }
}
