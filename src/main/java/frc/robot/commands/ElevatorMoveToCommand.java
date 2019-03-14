package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.elevator.Mode;
import frc.robot.utils.RobotTime;

public class ElevatorMoveToCommand extends Command {
  private Mode mode;
  private double duration;
  private Double startTime;
  private EndOption endOption;

  // Invalid is if the command was called without being at home
  private boolean invalid = false;
  private boolean motionProfileFinished = false;
  private boolean reachedMode = false;

  ElevatorMoveToCommand(Mode mode, EndOption endOption) {
    super("Elevator Move To Mode");
    requires(Robot.elevator);
    requires(Robot.motionProfile);
    this.mode = mode;
    this.endOption = endOption;
  }

  private ElevatorMoveToCommand(Mode mode) {
    this(mode, EndOption.END);
  }

  @Override
  protected void initialize() {
    invalid = false;
    Mode reachedMode = Robot.elevator.getReachedMode();
    // If we're in a manual mode and we're trying to go anywhere but home and we're not at home, disallow it
    if (!reachedMode.isLevel() && mode != Mode.HOME && !Robot.elevator.hasReachedPositionInches(0, 5)) {
      invalid = true;
    // If reached mode isn't HOME and we're trying to go anywhere else but HOME, disallow it
    } else if (reachedMode.isLevel() && reachedMode != Mode.HOME && mode != Mode.HOME) {
      invalid = true;
    }

    if (!invalid) {
      duration = mode.getDuration(reachedMode);
    }

    motionProfileFinished = false;
    startTime = RobotTime.getFPGASeconds();
  }

  @Override
  protected void execute() {
    if (invalid) {
      return;
    }

    // Run motion profile for its duration, then run position PID loop
    if (!motionProfileFinished) {
      double elapsedTime = RobotTime.getFPGASeconds() - startTime;
      if (elapsedTime <= duration) {
        // === Run the motion profile

        double inchesPerSec;
        boolean negative = false;
        // If we're going up to HIGH or we're going down to HOME from HIGH
        if (mode == Mode.HIGH || (Robot.elevator.getReachedMode() == Mode.HIGH && mode == Mode.HOME)) {
          // == Use high's special profile
          inchesPerSec = Robot.motionProfile.calculateElevatorHighMotion(startTime);
          if (mode == Mode.HOME) {
            negative = true;
          }
        } else {
          // == Use logistic profile

          double inches = mode.getPositionSetpointInches();
          // @ If we're going home, just have the distance be how high we are
          if (mode == Mode.HOME) {
            Mode reached = Robot.elevator.getReachedMode();
            inches = reached.isLevel() ? reached.getPositionSetpointInches() : Robot.elevator.getHeightInches();
            negative = true;
          }
          if (inches < 0) {
            negative = true;
          }
          inchesPerSec = Robot.motionProfile.calculateElevatorLogisticMotion(Math.abs(inches), duration, startTime);
        }

        // Convert to ticks per 100ms
        double ticksPer100ms = Robot.motionProfile.getEncoderSetpoint(inchesPerSec);

        // @ Add negative sign if needed
        if (negative) {
          ticksPer100ms *= -1;
        }

        // Set target velocity on TalonSRX
        Robot.elevator.setTargetVelocity(mode, ticksPer100ms);
      } else {
        // Mark that we're done running the motion profile
        motionProfileFinished = true;
      }
    } else {
      // === Run position PID loop
      int setpoint = mode.getPositionSetpointTicks();
      Robot.elevator.setTargetPosition(mode, setpoint);
      reachedMode = Robot.elevator.hasReachedMode(mode, true);
    }
  }

  @Override
  protected boolean isFinished() {
    if (invalid) {
      return true;
    }

    // If we're trying to continuously stay at this point, never end
    if (endOption == EndOption.CONTINUOUS_STAY) {
      return false;
    }
    // For CONTINUOUS_STAY_COMMAND or END, end once we reach
    return reachedMode;
  }

  @Override
  protected void end() {
    if (endOption == EndOption.CONTINUOUS_STAY_COMMAND) {
      new ElevatorStayAtCommand(mode).start();
    }
  }

  public enum EndOption {
    /**
     * Make this command never end, keeping elevator at this position
     */
    CONTINUOUS_STAY,
    /**
     * Make this command start a {@link ElevatorStayAtCommand} when it reaches the setpoint
     */
    CONTINUOUS_STAY_COMMAND,
    /**
     * End this command when it reaches the setpoint
     */
    END
  }
}
