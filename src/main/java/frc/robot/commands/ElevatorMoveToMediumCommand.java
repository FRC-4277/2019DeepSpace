/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Elevator2;
import frc.robot.utils.RobotTime;

public class ElevatorMoveToMediumCommand extends Command {
  private static final double DURATION = 1.0;
  private double startTime;
  private boolean motionProfileFinished = false;

  public ElevatorMoveToMediumCommand() {
    requires(Robot.elevator);
    requires(Robot.motionProfile);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    startTime = RobotTime.getFPGASeconds();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    double elapsedTime = RobotTime.getFPGASeconds() - startTime;
    if (elapsedTime <= DURATION) {
      // Medium uses logistic motion profile
      double inchesPerSec = Robot.motionProfile.calculateElevatorLogisticMotion(Elevator2.Mode.MEDIUM.getPositionSetpointInches(), DURATION, startTime);
      // Convert to ticks/100ms
      double ticksPer100ms = Robot.motionProfile.getEncoderSetpoint(inchesPerSec);
      // Set target velocity
      Robot.elevator.setTargetVelocity(Elevator2.Mode.MEDIUM, ticksPer100ms);
      motionProfileFinished = true;
    } else {
      // Now we're doing position based
      int setpoint = Elevator2.Mode.MEDIUM.getPositionSetpointTicks();
      Robot.elevator.setTargetPosition(Elevator2.Mode.MEDIUM, setpoint);
    }
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return motionProfileFinished && /* TODO : Check if reached encoder position*/;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
