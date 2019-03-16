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
    // POSITION & SIZE
    .withPosition(7, 2)
    .withSize(2, 2)
		.getEntry();
  }
  
  private boolean hasReachedLine = false;
  private boolean invert; 

  public JoystickDriveStopOnLineCommand() {
    requires(Robot.mecanumDrive);
    invert = false;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    hasReachedLine = false;
    Robot.lineUpGyro.reset();
    entry.setBoolean(true);

    double navXRadians = Robot.navX.getAngle() * Math.PI / 180;
    invert = Math.cos(navXRadians) < -0.2;
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    if (hasReachedLine) {
      return;
    }
    Result result = Robot.cargoColorSensor.readAll();
    Result result2 = Robot.hatchColorSensor.readAll();
    if (result.getClear() > 10 || result2.getClear() > 10) {
      OI.xboxController1.setRumble(RumbleType.kRightRumble, 1.0);
      Robot.mecanumDrive.mecanumDrive(0, 0, 0, false);
      hasReachedLine = true;
      return;
    }
    double x = OI.driveStick.getX(); 
    x *= 0.5;
    double y = OI.driveStick.getY();
    y *= 0.3;
    if (invert) {
      x *= -1;
      y *= -1;
    }
    Robot.mecanumDrive.mecanumDrive(x, y, 0, Robot.lineUpGyro.getAngle(), true);
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
