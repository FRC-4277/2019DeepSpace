/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import frc.robot.subsystems.Elevator.Mode;

public class ElevatorMoveToLoadingStationCommand extends ElevatorMoveToCommand {
  public ElevatorMoveToLoadingStationCommand(boolean runContinuously) {
    super(Mode.LOADING_STATION, runContinuously);
  }
}
