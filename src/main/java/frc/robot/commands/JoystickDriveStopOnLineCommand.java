/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.ColorProximitySensor.Result;

public class JoystickDriveStopOnLineCommand extends Command {
  public static NetworkTableEntry entry;
  static {
    entry = Shuffleboard.getTab("General")
		.add("Line Up Mode", false)
		.withWidget("Boolean Box")
		.getEntry();
  }
  
  private boolean hasReachedLine = false;

  public JoystickDriveStopOnLineCommand() {
    requires(Robot.mecanumDrive);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.lineUpGyro.reset();
    entry.setBoolean(true);
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
    // https://www.desmos.com/calculator/fflssd6tlk
    double x = OI.driveStick.getX(); 
    x = (0.8 * Math.pow(x, 1 / 3.0)) - (0.3 * Math.pow(x, 1 / 9.0));
    double y = OI.driveStick.getY();
    y *= 0.3;
    Robot.mecanumDrive.mecanumDrive(x, y, 0, Robot.lineUpGyro.getAngle(), true);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return hasReachedLine;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    OI.xboxController1.setRumble(RumbleType.kRightRumble, 1.0);
    Robot.mecanumDrive.mecanumStop();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
