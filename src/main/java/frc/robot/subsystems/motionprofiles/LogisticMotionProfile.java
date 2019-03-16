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
    private double duration = 0;

    private double upperError;
    private double lowerError;

    public LogisticMotionProfile(double distance, double duration){
        this.distance = distance;
        this.duration = duration;

        this.upperError = 0.01;
        this.lowerError = 0.05;
    }
    
    public LogisticMotionProfile(double distance, double duration, double upperError, double lowerError){
        this.distance = distance;
        this.duration = duration;

        this.upperError = upperError;
        this.lowerError = lowerError;
    }
    //TODO: MAKE ERROR MARGINS CHANGEABLE
    //Calculate the X component of the motion profile
    public Double calculateVelocity(double startTime){

        //zeros the profile if we dont want to move in X direction
        if (distance == 0.0) return 0.0;
        //Calculate the current time relative to the start of the function
        double timeElapsed = (RobotController.getFPGATime() - startTime) / 1000000;
        //Calculate constant k 
        double k = Math.log((distance * (lowerError / (distance - lowerError)) / (distance - upperError)) - (lowerError / (distance - lowerError))) / (-distance * duration);
        //Calculate constant c
        double c = lowerError / ((distance * k) - (k * lowerError));
        //calculate the position graph of the profile
        double x = (distance * k * c) / ((k * c) + Math.pow(Math.E, (-distance * k * timeElapsed)));
        //Calculates velocity
        double v = k * x * (distance - x);

        return v;
    }

    public Double calculateDelayedVelocity(double startTime, double delaySeconds){

        //zeros the profile if we dont want to move in X direction
        if (distance == 0.0) return 0.0;
        //Calculate the current time relative to the start of the function
        double timeElapsed = ((RobotController.getFPGATime() - startTime) / 1000000) - delaySeconds;
        //Calculate constant k 
        double kR = Math.log((distance * (lowerError / (distance - lowerError)) / (distance - upperError)) - (lowerError / (distance - lowerError))) / (-distance * duration);
        //Calculate constant c
        double cR = lowerError / ((distance * kR) - (kR * lowerError));
        //calculate the position graph of the profile
        double xR = (distance * kR * cR) / ((kR * cR) + Math.pow(Math.E, (-distance * kR * timeElapsed)));
        //Calculates velocity
        double vR = kR * xR * (distance - xR);

        return vR;
    }

    public double getSetpoint(){
        return distance;
    }


    
}
