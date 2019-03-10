package frc.robot.commands;

import frc.robot.subsystems.Elevator;

public class ElevatorMoveToHighCommand extends ElevatorMoveToCommand {
  public ElevatorMoveToHighCommand(EndOption endOption) {
    super(Elevator.Mode.HIGH, endOption);
  }

  public ElevatorMoveToHighCommand() {
    this(EndOption.END);
  }
}
