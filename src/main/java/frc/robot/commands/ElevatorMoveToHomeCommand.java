package frc.robot.commands;

import frc.robot.subsystems.elevator.Mode;

public class ElevatorMoveToHomeCommand extends ElevatorMoveToCommand {
  public ElevatorMoveToHomeCommand(EndOption endOption) {
    super(Mode.HOME, endOption);
  }

  public ElevatorMoveToHomeCommand() {
    this(EndOption.END);
  }
}
