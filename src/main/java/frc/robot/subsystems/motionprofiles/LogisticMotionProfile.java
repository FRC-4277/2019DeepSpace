/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems.motionprofiles;

import edu.wpi.first.wpilibj.RobotController;

/**
 * This class creates a profile as an object. 
 * This allows for multiple profiles to be created at once and exected at different times.
 */
public class LogisticMotionProfile {

    private double distanceX = 0;
    private double distanceY = 0;
    private double rotationTarget = 0;
    private double duration = 0;

    public LogisticMotionProfile(Double xDistance, Double yDistance, Double duration){
        distanceX = xDistance;
        distanceY = yDistance;
        this.duration = duration;
    }
    public LogisticMotionProfile(Double xDistance, Double yDistance, Double rDegrees, Double duration){
        distanceX = xDistance;
        distanceY = yDistance;
        rotationTarget = rDegrees;
        this.duration = duration;
    }
    public LogisticMotionProfile(Double rDegrees, Double duration){
        rotationTarget = rDegrees;
        this.duration = duration;
    }
    //Calculate the X component of the motion profile
    public Double calculateXVelocity(Double startTime){

        //zeros the profile if we dont want to move in X direction
        if (distanceX == 0.0) return 0.0;
        //Calculate the current time relative to the start of the function
        double timeElapsed = (RobotController.getFPGATime() - startTime) / 1000000;
        //Calculate constant k 
        double kX = Math.log((distanceX * (0.05 / (distanceX - 0.05)) / (distanceX - 0.01)) - (0.05 / (distanceX - 0.05))) / (-distanceX * duration);
        //Calculate constant c
        double cX = 0.05 / ((distanceX * kX) - (kX * 0.05));
        //calculate the position graph of the profile
        double xX = (distanceX * kX * cX) / ((kX * cX) + Math.pow(Math.E, (-distanceX * kX * timeElapsed)));
        //Calculates velocity
        double vX = kX * xX * (distanceX - xX);

        return vX;
    }
    //Calculate the Y component of the motion profile
    public Double calculateYVelocity(Double startTime){

        //zeros the profile if we dont want to move in X direction
        if (distanceY == 0.0) return 0.0;
        //Calculate the current time relative to the start of the function
        double timeElapsed = (RobotController.getFPGATime() - startTime) / 1000000;
        //Calculate constant k 
        double kY = Math.log((distanceY * (0.05 / (distanceY - 0.05)) / (distanceY - 0.01)) - (0.05 / (distanceY - 0.05))) / (-distanceY * duration);
        //Calculate constant c
        double cY = 0.05 / ((distanceY * kY) - (kY * 0.05));
        //calculate the position graph of the profile
        double xY = (distanceY * kY * cY) / ((kY * cY) + Math.pow(Math.E, (-distanceY * kY * timeElapsed)));
        //Calculates velocity
        double vY = kY * xY * (distanceY - xY);

        return vY;
    }
    //Calculate the rotational component of the motion profile 
    public Double calculateRotationalVelocity(Double startTime){

        //zeros the profile if we dont want to move in X direction
        if (rotationTarget == 0.0) return 0.0;
        //Calculate the current time relative to the start of the function
        double timeElapsed = (RobotController.getFPGATime() - startTime) / 1000000;
        //Calculate constant k 
        double kR = Math.log((rotationTarget * (0.05 / (rotationTarget - 0.05)) / (rotationTarget - 0.01)) - (0.05 / (rotationTarget - 0.05))) / (-rotationTarget * duration);
        //Calculate constant c
        double cR = 0.05 / ((rotationTarget * kR) - (kR * 0.05));
        //calculate the position graph of the profile
        double xR = (rotationTarget * kR * cR) / ((kR * cR) + Math.pow(Math.E, (-rotationTarget * kR * timeElapsed)));
        //Calculates velocity
        double vR = kR * xR * (rotationTarget - xR);

        return vR;
    }
    public Double calculateRotationalVelocity(Double startTime, Double delaySeconds){

        //zeros the profile if we dont want to move in X direction
        if (rotationTarget == 0.0) return 0.0;
        //Calculate the current time relative to the start of the function
        double timeElapsed = ((RobotController.getFPGATime() - startTime) / 1000000) - delaySeconds;
        //Calculate constant k 
        double kR = Math.log((rotationTarget * (0.05 / (rotationTarget - 0.05)) / (rotationTarget - 0.01)) - (0.05 / (rotationTarget - 0.05))) / (-rotationTarget * duration);
        //Calculate constant c
        double cR = 0.05 / ((rotationTarget * kR) - (kR * 0.05));
        //calculate the position graph of the profile
        double xR = (rotationTarget * kR * cR) / ((kR * cR) + Math.pow(Math.E, (-rotationTarget * kR * timeElapsed)));
        //Calculates velocity
        double vR = kR * xR * (rotationTarget - xR);

        return vR;
    }



    
}
