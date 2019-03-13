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

    private double distance = 0;
    //private double distanceY = 0;
    private double rotationTarget = 0;
    private double duration = 0;

    public LogisticMotionProfile(Double distance, Double duration){
        this.distance = distance;
        this.duration = duration;
    }
    
    //TODO: MAKE ERROR MARGINS CHANGEABLE
    //Calculate the X component of the motion profile
    public Double calculateVelocity(Double startTime){

        //zeros the profile if we dont want to move in X direction
        if (distance == 0.0) return 0.0;
        //Calculate the current time relative to the start of the function
        double timeElapsed = (RobotController.getFPGATime() - startTime) / 1000000;
        //Calculate constant k 
        double k = Math.log((distance * (0.05 / (distance - 0.05)) / (distance - 0.01)) - (0.05 / (distance - 0.05))) / (-distance * duration);
        //Calculate constant c
        double c = 0.05 / ((distance * k) - (k * 0.05));
        //calculate the position graph of the profile
        double x = (distance * k * c) / ((k * c) + Math.pow(Math.E, (-distance * k * timeElapsed)));
        //Calculates velocity
        double v = k * x * (distance - x);

        return v;
    }

    public Double calculateDelayedVelocity(Double startTime, Double delaySeconds){

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
