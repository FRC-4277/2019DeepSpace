/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Elevator.Mode;

public class ElevatorMoveToCommand extends Command {
  private Mode mode;
  private boolean runContinuously;

  public ElevatorMoveToCommand(Mode mode, boolean runContinuously) {
    this(mode, runContinuously, true);
  }

  public ElevatorMoveToCommand(Mode mode, boolean runContinuously, boolean requireElevator) {
    if (requireElevator) {
      requires(Robot.elevator);
    }
    this.mode = mode;
    this.runContinuously = runContinuously;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    if (Robot.elevator.getMode() == Mode.HOME) {
      Robot.elevator.resetEncoder();
    }
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    Robot.elevator.goToMode(mode);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if (runContinuously) {
      return false;
    } else {
      return Robot.elevator.hasReachedMode(mode);
    }
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
