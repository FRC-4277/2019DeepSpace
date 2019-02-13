/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.wpilibj.RobotController;

/**
 * Add your docs here.
 */
public class MotionProfile extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  
  //feet per second
  public double maxXSpeed;
  //feet per second
  public double maxYSpeed = 7.7;
  //degrees per second
  public double maxZSpeed;


  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  public Double[] calculateLMVelocities(Double distanceX, Double distanceY,Double rotationZ, Double durration, Double startTime) { 
    //this integer controls a switch that will zero the motion profiles if there is no desired movement in a given direction
    //Avoids returning null values
    int condition = 0;
    
    //creating a vector to hold motor write values and robot velocity components
    Double[] velocityArray = new Double[3];
    
    //calculating the current time from when the robot starts moving.
    double timeEllapsed = (RobotController.getFPGATime() - startTime)/1000000;
    
    //calculating constant k that defines the motion of the robot in the x and y direction
    double kX = Math.log((distanceX*(0.05/(distanceX-0.05))/(distanceX-0.01))-(0.05/(distanceX-0.05)))/(-distanceX*durration);
    double kY = Math.log((distanceY*(0.05/(distanceY-0.05))/(distanceY-0.01))-(0.05/(distanceY-0.05)))/(-distanceY*durration);
    double kZ = Math.log((rotationZ*(0.05/(rotationZ-0.05))/(rotationZ-0.01))-(0.05/(rotationZ-0.05)))/(-rotationZ*durration);

    //calculates the constant c that defines the initial conditions for the motion profile
    double cX = 0.05 / ((distanceX*kX)-(kX*0.05));
    double cY = 0.05 / ((distanceY*kY)-(kY*0.05));
    double cZ = 0.05 / ((rotationZ*kZ)-(kZ*0.05));

    //creates the motion profile for the robots motion
    double xX = (distanceX*kX*cX)/((kX*cX)+Math.pow(Math.E, (-distanceX*kX*timeEllapsed)));
    double xY = (distanceY*kY*cY)/((kY*cY)+Math.pow(Math.E, (-distanceY*kY*timeEllapsed)));
    double xZ = (distanceY*kZ*cZ)/((kZ*cZ)+Math.pow(Math.E, (-rotationZ*kZ*timeEllapsed)));

    //creates the velocity motion profile for the robots motion
    double vX = kX*xX*(distanceX - xX);
    double vY = kY*xY*(distanceY - xY);
    double vZ = kZ*xZ*(rotationZ - xZ);

    //logic that controls the switch that zeros outputs when needed
    if (rotationZ == 0){
      if(distanceX == 0)  condition = 1;
      else if(distanceY == 0) condition = 2;
      else if(distanceX != 0 && distanceY != 0) condition = 3;
    }
    else if(rotationZ != 0){
      if(distanceX == 0)  condition = 4;
      else if(distanceY == 0) condition = 5;
      else if(distanceX != 0 && distanceY != 0) condition = 6;
    }

    //makes sure that no null values are assigned as velocities
    //This could be replaced with an enum or lots of conditional logic if one desires to do so
    switch(condition){
      case 0: velocityArray[0] = 0.0;
              velocityArray[1] = 0.0;
              velocityArray[2] = 0.0;
              break;
      case 1: velocityArray[0] = 0.0;
              velocityArray[1] = vY;
              velocityArray[2] = 0.0;
              break;
      case 2: velocityArray[0] = vX;
              velocityArray[1] = 0.0;
              velocityArray[2] = 0.0;
              break;
      case 3: velocityArray[0] = vX;
              velocityArray[1] = vY;
              velocityArray[2] = 0.0;
              break;
      case 4: velocityArray[0] = 0.0;
              velocityArray[1] = vY;
              velocityArray[2] = vZ;
              break;
      case 5: velocityArray[0] = vX;
              velocityArray[1] = 0.0;
              velocityArray[2] = vZ;
              break;
      case 6: velocityArray[0] = vX;
              velocityArray[1] = vY;
              velocityArray[2] = vZ;
              break;

    }
    return  velocityArray;
  }

  //the following method takes the velocity vector components stroed in the velocityArray and converts them into drive values that mimic joystick values
  //In this example they will be linear and percentages. The important thing is that drive values are calculated using a 
  //calibrated transfer function 
  public Double[] calculateDriveValues(Double distanceX, Double distanceY,Double rotationZ, Double durration, Double startTime){
    Double[] driveValues = new Double[3];
    
    //The following three lines calculate the percentages that will plugged into a standard mecanumDrive commmand
    driveValues[0] = calculateLMVelocities(distanceX, distanceY, rotationZ, durration, startTime)[0] / maxXSpeed;
    driveValues[1] = calculateLMVelocities(distanceX, distanceY, rotationZ, durration, startTime)[1] / maxYSpeed;
    driveValues[2] = calculateLMVelocities(distanceX, distanceY, rotationZ, durration, startTime)[2] / maxZSpeed;

    return driveValues;
  }

}