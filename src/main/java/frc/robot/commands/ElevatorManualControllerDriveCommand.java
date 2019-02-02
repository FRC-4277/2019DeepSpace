/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.subsystems.Elevator.Mode;

public class ElevatorManualControllerDriveCommand extends Command {
  private static final double JOYSTICK_THRESHOLD = 0.30;
  
  public ElevatorManualControllerDriveCommand() {
    requires(Robot.elevator);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.elevator.goToMode(Mode.MANUAL_CONTROL);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    double manualPower = OI.xboxController.getRawAxis(5);
    if (Math.abs(manualPower) >= JOYSTICK_THRESHOLD) {
      Robot.elevator.drive(manualPower);
    }
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
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
