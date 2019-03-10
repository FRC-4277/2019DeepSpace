package frc.robot.commands;

import frc.robot.subsystems.Elevator;

public class ElevatorMoveToMediumCommand extends ElevatorMoveToCommand {
  public ElevatorMoveToMediumCommand(EndOption endOption) {
    super(Elevator.Mode.MEDIUM, endOption);
  }

  public ElevatorMoveToMediumCommand() {
    this(EndOption.END);
  }
}
