/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems.motionprofiles;

/**
 * Add your docs here.
 */
public class CurveParameters {

    private double rotationTarget;
    private double duration;
    private double delay;

    public CurveParameters(double rotationTarget, double duration, double delayFromStartTime){
        this.rotationTarget = rotationTarget;
        this.duration = duration;
        this.delay = delayFromStartTime;
    }

    public double[] getParamArray(){
        double[] paramArray = new double[3];
        paramArray[0] = rotationTarget;
        paramArray[1] = duration;
        paramArray[2] = delay;

        return paramArray;
    }

    public double getRotationTarget(){
        return rotationTarget;
    }

    public double getDuration(){
        return duration;
    }

    public double getDelay(){
        return delay;
    }
}
