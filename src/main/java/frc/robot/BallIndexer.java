package frc.robot;

import com.revrobotics.CANSparkMax; //SparkMax motor controller, not a SparkMax motor
import com.revrobotics.CANSparkMaxLowLevel.MotorType;



public class BallIndexer {
    // private static final double kSpeedMultiplier = 0.1;

    // private static final int kFrontLeftChannel = 1;
    // private static final int kRearLeftChannel = 4;
    // private static final int kFrontRightChannel = 3;
    // private static final int kRearRightChannel = 2;
    // private boolean inverted;

    private static final int leftConveyorChannel = 0;
    private static final int rightConveyorChannel = 0;

    private static final double speedMultiplier = 1;

    public static final double advanceSpeed = 10;
    public static final double stopSpeed = 0;

    private CANSparkMax[] motors;


    // It is assumed that both motors are for the left and
    // right conveyor belts, and that there is only one
    // motor zone

    // private CANSparkMax[] motors;

    public BallIndexer() {
        this.motors = new CANSparkMax[2];

        this.motors[0] = new CANSparkMax(BallIndexer.leftConveyorChannel, MotorType.kBrushless);
        this.motors[1] = new CANSparkMax(BallIndexer.rightConveyorChannel, MotorType.kBrushless);
        this.motors[1].setInverted(true); //Switch this to this.motors[0] if it goes backwards

        // TODO: Set up a sensor object that monitors if a ball is in the indexer
  
    }

    //Methods go here

    public boolean isBallPrepared() {
        //TODO: Read ball sensor value and return it
        return true;
    }

    public void setIndexerMechanismSpeed(double speed) {
        this.motors[0].set(speed * BallIndexer.speedMultiplier);
    }

    public void advanceBallIfNecessary() {
        //FUTURE: If necessary, add a timer and decrease the ball's speed slowly if the sudden motor stop causes problems
        if (this.isBallPrepared()) {
            this.setIndexerMechanismSpeed(BallIndexer.stopSpeed);
        } else {
            this.setIndexerMechanismSpeed(BallIndexer.advanceSpeed);
        }
    }


}