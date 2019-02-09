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
import frc.robot.ColorProximitySensor.Result;

public class JoystickDriveStopOnLineCommand extends Command {
  private boolean hasReachedLine = false;

  public JoystickDriveStopOnLineCommand() {
    requires(Robot.mecanumDrive);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    if (hasReachedLine) {
      return;
    }
    Result result = Robot.colorProximitySensor.readAll();
    if (result.getClear() > 10) {
      hasReachedLine = true;
      return;
    }
    double slider = OI.driveStick.getRawAxis(3);
		if (slider >= 0) 
			Robot.mecanumDrive.mecanumDriveJoystick(OI.driveStick, false);
		else if (slider < 0)
			Robot.mecanumDrive.fieldOrientedMecanumDriveJoystick(OI.driveStick, Robot.navX.getAngle(), false);
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
    hasReachedLine = false;
  }
}
