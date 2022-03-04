package frc.robot;

import edu.wpi.first.wpilibj.Servo;

public class BallIndexer {
    public Servo servo;

    public BallIndexer() {
        servo = new Servo(Constants.INDEXER_SERVO_PORT);
    }

    public void extend() {
        servo.set(Constants.INDEXER_EXTENSION);
    }

    public void retract() {
        servo.set(0);
    }
}