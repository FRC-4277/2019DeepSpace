/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

//Motion Profile: https://www.desmos.com/calculator/rov7zwzt2f

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.subsystems.elevator.Elevator;
import frc.robot.subsystems.motionprofiles.LogisticMotionProfile;
import frc.robot.utils.RobotTime;

/**
 * Add your docs here.
 */
public class MotionProfile extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  //feet per second
  public double maxXSpeed = 100000;// This is high so that we get zero rather than null must be changes to use
  //feet per second
  public double maxYSpeed = 7.4;
  //degrees per second
  public double maxZSpeed = 100000;// This is high so that we get zero rather than null must be changes to use


  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }


  //the following method takes the velocity vector components stroed in the velocityArray and converts them into drive values that mimic joystick values
  //In this example they will be linear and percentages. The important thing is that drive values are calculated using a 
  //calibrated transfer function 

  public Double[] calculateDriveValues(LogisticMotionProfile profile, Double startTime) {
    Double[] driveValues = new Double[3];

    //The following three lines calculate the percentages that will plugged into a standard mecanumDrive commmand
    driveValues[0] = profile.calculateXVelocity(startTime) / maxXSpeed;
    driveValues[1] = profile.calculateYVelocity(startTime) / maxYSpeed;
    driveValues[2] = profile.calculateRotationalVelocity(startTime) / maxZSpeed;

    return driveValues;
  }

  public Double[] calculateDriveValuesRotation(LogisticMotionProfile profile, Double startTime, Double delay) {
    Double[] driveValues = new Double[3];

    //The following three lines calculate the percentages that will plugged into a standard mecanumDrive commmand
    driveValues[0] = profile.calculateXVelocity(startTime) / maxXSpeed;
    driveValues[1] = profile.calculateYVelocity(startTime) / maxYSpeed;
    driveValues[2] = profile.calculateRotationalVelocity(startTime, delay) / maxZSpeed;

    return driveValues;
  }


  /**
   * Calculates target velocity based on time.
   * Use this for going up to low, medium, or loading station
   *
   * @param height    In inches (positive)
   * @param duration  In seconds
   * @param startTime In seconds
   * @return Velocity in inches/sec (always positive)
   */
  public double calculateElevatorLogisticMotion(double height, double duration, double startTime) {

    final double topError = 0.25;
    final double bottomError = 0.15;

    double timeElapsed = RobotTime.getFPGASeconds() - startTime;

    double k = Math.log((height * (bottomError / (height - bottomError)) / (height - topError)) - (bottomError / (height - bottomError))) / (-height * duration);
    double c = bottomError / ((height * k) - (k * bottomError));

    double heightProfile = (height * k * c) / ((k * c) + Math.pow(Math.E, (-height * k * timeElapsed)));
    double heightVelocityProfile = k * heightProfile * (height - heightProfile);

    return heightVelocityProfile;
  }

  /**
   * Refer to https://www.desmos.com/calculator/hvgzmv6exl (look at v_elocity(x) for velocity)
   *
   * @param startTime In seconds
   * @return Velocity in inches/second (always positive)
   */
  public double calculateElevatorHighMotion(double startTime) {
    double timeElapsed = RobotTime.getFPGASeconds() - startTime;
    return ((9750.510515 * Math.pow(timeElapsed, 6)) / 6)
            + ((-37226.71326 * Math.pow(timeElapsed, 5)) / 5)
            + ((49867.42227 * Math.pow(timeElapsed, 4)) / 4)
            + ((-27249.45738 * Math.pow(timeElapsed, 3)) / 3)
            + ((4950.683735 * Math.pow(timeElapsed, 2)) / 2);
  }

  /**
   * @param velocity In inches per second
   * @return In encoder ticks per 100 ms
   */
  public double getEncoderSetpoint(double velocity) {
    // Convert inches/sec to ticks/sec
    double ticksPerSecond = Elevator.calculateTicks(velocity);
    // Convert ticks/sec to ticks/100ms and RETURN
    return ticksPerSecond / 10;
  }
}