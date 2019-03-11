package frc.robot.commands;

import frc.robot.subsystems.elevator.Mode;

public class ElevatorMoveToLoadingStationCommand extends ElevatorMoveToCommand {
  public ElevatorMoveToLoadingStationCommand(EndOption endOption) {
    super(Mode.LOADING_STATION, endOption);
  }

  public ElevatorMoveToLoadingStationCommand() {
    this(EndOption.END);
  }
}
