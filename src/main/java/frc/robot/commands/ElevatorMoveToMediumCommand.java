package frc.robot.commands;

import frc.robot.subsystems.elevator.Mode;

public class ElevatorMoveToMediumCommand extends ElevatorMoveToCommand {
  public ElevatorMoveToMediumCommand(EndOption endOption) {
    super(Mode.MEDIUM, endOption);
  }

  public ElevatorMoveToMediumCommand() {
    this(EndOption.END);
  }
}
