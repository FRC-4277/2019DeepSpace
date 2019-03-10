package frc.robot.commands;

import frc.robot.subsystems.Elevator;

public class ElevatorMoveToHomeCommand extends ElevatorMoveToCommand {
  public ElevatorMoveToHomeCommand(EndOption endOption) {
    super(Elevator.Mode.HOME, endOption);
  }

  public ElevatorMoveToHomeCommand() {
    this(EndOption.END);
  }
}
