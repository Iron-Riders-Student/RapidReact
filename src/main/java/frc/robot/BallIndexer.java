package frc.robot;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BallIndexer {
    private Servo servo;

    public BallIndexer() {
        servo = new Servo(0);
    }

    public void updateDashboard() {
        SmartDashboard.putNumber("indexer position", servo.getAngle());
    }

    public void extend() {
        servo.setAngle(45);
    }

    public void retract() {
        servo.setAngle(0);
    }
}