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
import frc.robot.subsystems.motionprofiles.CurveParameters;
import frc.robot.subsystems.motionprofiles.LogisticMotionProfile;

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
  double delay;
  double timeFromStart;
  
  LogisticMotionProfile[] rotations;
  double[] delayArray;
  boolean[] isThetaNegativeArray;

  LogisticMotionProfile xProfile;
  LogisticMotionProfile yProfile;
  LogisticMotionProfile rotationalProfile;

  int n;
  int counter;

    public DriveToCommand(double inputDistanceX, double inputDistanceY, double inputDegrees, double duration) {

      requires(Robot.mecanumDrive);
      
      n = 0;
    	
    	if(inputDistanceX < 0) isXNeg = true;
    	if(inputDistanceY < 0) isYNeg = true;
    	if(inputDegrees < 0) isThetaNeg = true;
    	
    	distanceX = Math.abs(inputDistanceX);
    	distanceY = Math.abs(inputDistanceY);
      rotationZ = Math.abs(inputDegrees);
      delay = 0.0;
      
      this.duration = duration;

      xProfile = new LogisticMotionProfile(distanceX, this.duration);
      yProfile = new LogisticMotionProfile(distanceY, this.duration);
      rotationalProfile = new LogisticMotionProfile(rotationZ, this.duration);
    	
    }

    public DriveToCommand(double inputDistanceX, double inputDistanceY, double duration, CurveParameters...rotationTargetsWithDurationsAndDelay){

      requires(Robot.mecanumDrive);

      n = rotationTargetsWithDurationsAndDelay.length;

      rotations = new LogisticMotionProfile[n];
      delayArray = new double[n];
      isThetaNegativeArray = new boolean[n];
    	
    	if(inputDistanceX < 0) isXNeg = true;
      if(inputDistanceY < 0) isYNeg = true;
      
      distanceX = Math.abs(inputDistanceX);
    	distanceY = Math.abs(inputDistanceY);
      
      this.duration = duration;

      for(int i = 0; i < n; i++){
        rotations[i] = new LogisticMotionProfile(Math.abs(rotationTargetsWithDurationsAndDelay[i].getRotationTarget()), rotationTargetsWithDurationsAndDelay[i].getDuration(), 1, 1);
        delayArray[i] = rotationTargetsWithDurationsAndDelay[i].getDelay();
        isThetaNegativeArray[i] = rotationTargetsWithDurationsAndDelay[i].getRotationTarget() < 0 ? true : false;
      }

      xProfile = new LogisticMotionProfile(distanceX, this.duration);
      yProfile = new LogisticMotionProfile(distanceY, this.duration);
      
      rotationalProfile = rotations[0];
      isThetaNeg = isThetaNegativeArray[0];
    }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    startTime = RobotController.getFPGATime();
    counter = 0;
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

    timeFromStart = (RobotController.getFPGATime() - startTime)/1000000;

    if(timeFromStart >= duration) {
      finish = true;
    }
    
    if(n>0){
      if (counter <= n-1){
      determineRotationalProfile();
      }
    }

    normalizeDrive();
    Robot.mecanumDrive.mecanumDrive(driveX, driveY, driveZ, Robot.navX.getAngle(), false);
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

  protected void determineRotationalProfile(){
    if(counter == n-1){
      rotationalProfile = rotations[counter];
      isThetaNeg = isThetaNegativeArray[counter];
      delay = delayArray[counter];
    }
    else if(timeFromStart >= delayArray[counter+1]){
      counter++;
      rotationalProfile = rotations[counter];
      isThetaNeg = isThetaNegativeArray[counter];
      delay = delayArray[counter];
    }
    else if (timeFromStart < delayArray[counter+1]){
      rotationalProfile = rotations[counter];
      isThetaNeg = isThetaNegativeArray[counter];
      delay = delayArray[counter];
    }
  }
  
  protected void normalizeDrive() {
    
    if(isXNeg) {
      driveX = -Robot.motionProfile.calculateDriveValues(xProfile, yProfile, rotationalProfile, startTime)[0];
    }
    else if(!isXNeg) {
      driveX = Robot.motionProfile.calculateDriveValues(xProfile, yProfile, rotationalProfile, startTime)[0];
    }
    
    if(isYNeg) {
      driveY = Robot.motionProfile.calculateDriveValues(xProfile, yProfile, rotationalProfile, startTime)[1];
    }
    else if (!isYNeg) {
      driveY = -Robot.motionProfile.calculateDriveValues(xProfile, yProfile, rotationalProfile, startTime)[1];
    }
    
    if(isThetaNeg) {
      driveZ = -Robot.motionProfile.calculateDriveValuesRotation(xProfile, yProfile, rotationalProfile, startTime, delay)[2];
    }
    else if(!isThetaNeg) {
      driveZ = Robot.motionProfile.calculateDriveValuesRotation(xProfile, yProfile, rotationalProfile, startTime, delay)[2];
    }
  }
}
