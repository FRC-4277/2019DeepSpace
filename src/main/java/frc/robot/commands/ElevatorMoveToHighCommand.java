package frc.robot.commands;

import frc.robot.subsystems.elevator.Mode;

public class ElevatorMoveToHighCommand extends ElevatorMoveToCommand {
  public ElevatorMoveToHighCommand(EndOption endOption) {
    super(Mode.HIGH, endOption);
  }

  public ElevatorMoveToHighCommand() {
    this(EndOption.END);
  }
}
