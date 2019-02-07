/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.map;

/**
 * Add your docs here.
 */
public class CloneRobotMap implements RobotMap {

    @Override
    public int getFrontLeftTalon() {
        return 61;
    }

    @Override
    public int getBackLeftTalon() {
        return 12;
    }

    @Override
    public int getFrontRightTalon() {
        return 60;
    }

    @Override
    public int getBackRightTalon() {
        return 58;
    }

    @Override
    public int getElevatorTalon() {
        return 59;
    }

    @Override
    public int getElevatorFollowerTalon() {
        return 2;
    }

    @Override
    public int getPCMId() {
        return 1;
    }
}
