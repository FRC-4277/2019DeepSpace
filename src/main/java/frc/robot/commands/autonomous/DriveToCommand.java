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
  Double[] delayArray;
  boolean[] isThetaNegativeArray;

  LogisticMotionProfile mainProfile;
  LogisticMotionProfile rotationalProfile;

  int n;
  int counter;

    public DriveToCommand(Double inputDistanceX, Double inputDistanceY, Double inputDegrees, Double inputDurration) {

      requires(Robot.mecanumDrive);
      
      n = 0;
    	
    	if(inputDistanceX < 0) isXNeg = true;
    	if(inputDistanceY < 0) isYNeg = true;
    	if(inputDegrees < 0) isThetaNeg = true;
    	
    	distanceX = Math.abs(inputDistanceX);
    	distanceY = Math.abs(inputDistanceY);
    	rotationZ = Math.abs(inputDegrees);
      
      duration = inputDurration;

      mainProfile = new LogisticMotionProfile(inputDistanceX, inputDistanceY, inputDegrees, duration);
      rotationalProfile = new LogisticMotionProfile(0.0, 0.0);
    	
    }

    public DriveToCommand(Double inputDistanceX, Double inputDistanceY, Double duration, Double[]...rotationTargetsWithDurationsAndDelay){

      requires(Robot.mecanumDrive);

      n = rotationTargetsWithDurationsAndDelay.length;

      rotations = new LogisticMotionProfile[n];
      delayArray = new Double[n];
      isThetaNegativeArray = new boolean[n];
    	
    	if(inputDistanceX < 0) isXNeg = true;
      if(inputDistanceY < 0) isYNeg = true;
      
      distanceX = Math.abs(inputDistanceX);
    	distanceY = Math.abs(inputDistanceY);
      
      this.duration = duration;

      for(int i = 0; i <= n; i++){
        rotations[i] = new LogisticMotionProfile(Math.abs(rotationTargetsWithDurationsAndDelay[i][0]), rotationTargetsWithDurationsAndDelay[i][1]);
        delayArray[i] = rotationTargetsWithDurationsAndDelay[i][2];
        isThetaNegativeArray[i] = rotationTargetsWithDurationsAndDelay[i][0] < 0 ? true : false;
      }

      mainProfile = new LogisticMotionProfile(distanceX, distanceY, this.duration);
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

    if(timeFromStart >= duration) {
      finish = true;
    }

    timeFromStart = (RobotController.getFPGATime() - startTime)/1000000;
    
    if(n>0){
      determineRotationalProfile();
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

    if(timeFromStart < delayArray[counter+1]){
      rotationalProfile = rotations[counter];
      isThetaNeg = isThetaNegativeArray[counter];
    }
    else if(timeFromStart >= delayArray[counter+1]){
      counter++;
      rotationalProfile = rotations[counter];
      isThetaNeg = isThetaNegativeArray[counter];
    }
  }
  
  protected void normalizeDrive() {
    
    if(isXNeg) {
      driveX = -Robot.motionProfile.calculateDriveValues(mainProfile, startTime)[0];
    }
    else if(!isXNeg) {
      driveX = Robot.motionProfile.calculateDriveValues(mainProfile,startTime)[0];
    }
    
    if(isYNeg) {
      driveY = Robot.motionProfile.calculateDriveValues(mainProfile, startTime)[1];
    }
    else if (!isYNeg) {
      driveY = -Robot.motionProfile.calculateDriveValues(mainProfile, startTime)[1];;
    }
    
    if(isThetaNeg) {
      driveZ = -Robot.motionProfile.calculateDriveValuesRotation(rotationalProfile, startTime, delay)[2];
    }
    else if(!isThetaNeg) {
      driveZ = Robot.motionProfile.calculateDriveValuesRotation(rotationalProfile, startTime, delay)[2];
    }
  }
}
