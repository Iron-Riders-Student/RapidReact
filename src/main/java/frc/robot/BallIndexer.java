package frc.robot;

import edu.wpi.first.wpilibj.Servo;

public class BallIndexer {
    public Servo servo;

    public BallIndexer() {
        servo = new Servo(Constants.INDEXER_SERVO_PORT);
    }

    public void extend() {
        servo.set(.3);//(Constants.INDEXER_EXTEND_ANGLE);
    }

    public void retract() {
        servo.set(0);
    }
}