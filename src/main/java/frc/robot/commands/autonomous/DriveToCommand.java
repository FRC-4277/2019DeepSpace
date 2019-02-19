/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Robot;

public class DriveToCommand extends Command {

  double startTime;
	
	boolean finish  = false;
	boolean isXNeg = false;
	boolean isYNeg = false;
	boolean isThetaNeg = false;
	
	double driveX;
	double driveY;
	double driveZ;
	
	double distanceX;
	double distanceY;
	double rotationZ;
  double duration;
  boolean useGyro;

    public DriveToCommand(double inputDistanceX, double inputDistanceY,
     double inputDegrees, double duration, boolean useGyro) {

    	requires(Robot.mecanumDrive);
    	
    	if(inputDistanceX < 0) isXNeg = true;
    	if(inputDistanceY < 0) isYNeg = true;
    	if(inputDegrees < 0) isThetaNeg = true;
    	
    	this.distanceX = Math.abs(inputDistanceX);
    	this.distanceY = Math.abs(inputDistanceY);
    	this.rotationZ = Math.abs(inputDegrees);
      
      this.duration = duration;
    	this.useGyro = useGyro;
    }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    startTime = RobotController.getFPGATime();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    	
    if((RobotController.getFPGATime() - startTime)/1000000 >= duration) {
      finish = true;
    }
    
    normalizeDrive();
    Robot.mecanumDrive.mecanumDrive(driveX, driveY, driveZ, useGyro ? Robot.navX.getAngle() : 0, false);
  }

  // Make this return true when this Command no longer needs to run execute()
  protected boolean isFinished() {
      return finish;
  }

  // Called once after isFinished returns true
  protected void end() {
    Robot.mecanumDrive.mecanumStop();
    finish = false;
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  protected void interrupted() {
    Robot.mecanumDrive.mecanumStop();
  }
  
  protected void normalizeDrive() {
    
    if(isXNeg) {
      driveX = -Robot.motionProfile.calculateDriveValues(distanceX, distanceY, rotationZ, duration, startTime)[0];
    }
    else if(!isXNeg) {
      driveX = Robot.motionProfile.calculateDriveValues(distanceX, distanceY, rotationZ, duration, startTime)[0];
    }
    
    // Y is opposite of other values to account for the
    // fact that RobotDrive's mecanum methods themselves invert y
    if(isYNeg) {
      driveY = Robot.motionProfile.calculateDriveValues(distanceX, distanceY, rotationZ, duration, startTime)[1];
    }
    else if (!isYNeg) {
      driveY = -Robot.motionProfile.calculateDriveValues(distanceX, distanceY, rotationZ, duration, startTime)[1];;
    }
    
    if(isThetaNeg) {
      driveZ = -Robot.motionProfile.calculateDriveValues(distanceX, distanceY, rotationZ, duration, startTime)[2];
    }
    else if(!isThetaNeg) {
      driveZ = Robot.motionProfile.calculateDriveValues(distanceX, distanceY, rotationZ, duration, startTime)[2];
    }
  }
}
