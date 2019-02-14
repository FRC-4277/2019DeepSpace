/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.autonomous;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.Robot;
import frc.robot.ColorProximitySensor.Result;

//import edu.wpi.first.wpilibj.GenericHID.RumbleType;
//import frc.robot.OI;

public class DriveStopOnLineCommand extends Command {
  public static NetworkTableEntry entry;
  private String direction;
  double x;
  static {
    entry = Shuffleboard.getTab("General")
		.add("Line Up Mode", false)
		.withWidget("Boolean Box")
		.getEntry();
  }
  
  private boolean hasReachedLine = false;

  public DriveStopOnLineCommand(Double speed, String rightORleft) {
    requires(Robot.mecanumDrive);
    direction = rightORleft;
    if (direction == "right" || direction == "Right" || direction == "RIGHT") x = speed;
    else if (direction == "left" || direction == "Left" || direction == "LEFT") x = -speed;
    else x = 0;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.lineUpGyro.reset();
    entry.setBoolean(true);
    hasReachedLine = false;
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
      hasReachedLine = true;
      return;
    }
    
    Robot.mecanumDrive.mecanumDrive(x, 0, 0, Robot.lineUpGyro.getAngle(), true);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return hasReachedLine;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    //OI.xboxController1.setRumble(RumbleType.kRightRumble, 1.0);
    Robot.mecanumDrive.mecanumStop();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    Robot.mecanumDrive.mecanumStop();
  }
}
