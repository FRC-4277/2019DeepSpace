package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.elevator.Mode;

public class ElevatorStayAtCommand extends Command {
  private Mode mode;

  public ElevatorStayAtCommand(Mode mode) {
    super("Elevator Stay At Mode");
    this.mode = mode;
  }

  @Override
  protected void execute() {
    // == Run position PID loop
    int setpoint = mode.getPositionSetpointTicks();
    Robot.elevator.setTargetPosition(mode, setpoint);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
