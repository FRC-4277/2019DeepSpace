package frc.robot.commands;

import frc.robot.subsystems.Elevator;

public class ElevatorMoveToLoadingStationCommand extends ElevatorMoveToCommand {
  public ElevatorMoveToLoadingStationCommand(EndOption endOption) {
    super(Elevator.Mode.LOADING_STATION, endOption);
  }

  public ElevatorMoveToLoadingStationCommand() {
    this(EndOption.END);
  }
}
