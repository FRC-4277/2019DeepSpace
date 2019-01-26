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
public class CompetitionRobotMap implements RobotMap {

    @Override
    public int getFrontLeftTalon() {
        return 21;
    }

    @Override
    public int getBackLeftTalon() {
        return 22;
    }

    @Override
    public int getFrontRightTalon() {
        return 23;
    }

    @Override
    public int getBackRightTalon() {
        return 24;
    }

    @Override
    public int getElevatorTalon() {
        return -1;
    }

    @Override
    public int getElevatorFollowerTalon() {
        return -1;
    }
}
