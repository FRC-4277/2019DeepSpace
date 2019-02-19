/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import frc.robot.subsystems.Elevator.Mode;

public class ElevatorMoveToHighCommand extends ElevatorMoveToCommand {
  public ElevatorMoveToHighCommand(boolean runContinuously) {
    super(Mode.HIGH, runContinuously);
  }

  public ElevatorMoveToHighCommand(boolean runContinuously, boolean requireElevator) {
    super(Mode.HIGH, runContinuously, requireElevator);
  }
}
