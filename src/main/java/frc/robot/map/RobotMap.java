/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.map;



/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public interface RobotMap {
	public int getFrontLeftTalon();
	public int getBackLeftTalon();
	public int getFrontRightTalon();
	public int getBackRightTalon();
	
	public int getElevatorTalon();
	public int getElevatorFollowerTalon();
	public int getHatchPanelId();
	public int getCargoID();
}
