/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.ColorProximitySensor.Result;

public class JoystickDriveStopOnLineCommand extends Command {
  public static final int THRESHOLD = 12;
  public static NetworkTableEntry entry, linedUpEntry;
  static {
    entry = Shuffleboard.getTab("General")
		.add("Line Up Mode", false)
    .withWidget("Boolean Box")
    // POSITION & SIZE
    .withPosition(4, 2)
    .withSize(2, 2)
    .getEntry();

    linedUpEntry = Shuffleboard.getTab("General")
    .add("**Lined** Up Mode", false)
    .withWidget("Boolean Box")
    .withProperties(Map.of(
      "Color when false", "#ffffff", // White when false
      "Color when true", "#0004ff" // Blue when true
    ))
    // POSITION & SIZE
    .withPosition(6, 2)
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
    linedUpEntry.setBoolean(false);

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
    if (result.getClear() > THRESHOLD || result2.getClear() > THRESHOLD) {
      OI.xboxController1.setRumble(RumbleType.kRightRumble, 1.0);
      Robot.mecanumDrive.mecanumDrive(0, 0, 0, false);
      hasReachedLine = true;
      linedUpEntry.setBoolean(true);
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
    linedUpEntry.setBoolean(false);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    end();
  }
}
