package frc.robot;

import edu.wpi.first.wpilibj.Servo;

public class BallIndexer {
    private Servo servo;

    public BallIndexer() {
        servo = new Servo(Constants.INDEXER_SERVO_PORT);
    }

    public void extend() {
        servo.setAngle(Constants.INDEXER_EXTEND_ANGLE);
    }

    public void retract() {
        servo.setAngle(0);
    }
}