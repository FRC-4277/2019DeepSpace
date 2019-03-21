package frc.robot.subsystems.elevator;

import java.util.function.Function;

public enum Mode {
  // TODO : Tune durations
  /**
   * Mode where elevator is controlled with joystick
   */
  MANUAL_CONTROL("Manual Control"),
  /**
   * Level where we're at the lowest and hitting the limit switch.
   * Also where we're placing cargo or hatches on rocket low ports.
   * Also where we're taking hatches from loading station.
   *
   * Duration Function Explanation:
   *    Duration going to home level is 30% more than going up to that level from home
   */
  //(mode) -> mode.name().equals("HOME") ? 0.25 : (mode.getDuration(Mode.valueOf("HOME")) * 1.3))
  HOME("Home", -2, 0.5, mode -> {
    if (!mode.isLevel()) {
      return 0.0;
    }
    // If we're at home, and we're trying to go home:
    if (mode.name().equals("HOME")) {
      return 0.25;
      // If we're at high and trying to go home:
    } else if (mode.name().equals("HIGH")) {
      return 1.5;
    }
    // If we're at a different level and trying to go home, make the duration 30% more than going up
    return mode.getDuration(Mode.valueOf("HOME")) * 1.3;
  }),
  /**
   * Level where we're shooting cargo into cargo ship or taking a cargo from loading station
   */
  LOADING_STATION("Loading Station", 16, 2.0, (mode) -> 0.7),
  /**
   * Level where we're placing cargo or hatches on rocket middle ports
   */
  MEDIUM("Medium", 28, 2.0, (mode) -> 1.1),
  /**
   * Level where we're placing cargo or hatches on rocket high ports
   */
  HIGH("High", 49, 2.0, (mode) -> 1.3);

  private String name;
  private boolean isLevel;
  // Position PID setpoint
  private double inches;
  private Integer encoderTicks;
  // Position PID margin of error
  private double errorMarginInches;
  private Integer errorMarginTicks;
  // Function that tells a duration, starting at a specific mode (we're starting at home for all modes)
  private Function<Mode, Double> profileDuration;

  Mode(String name, boolean isLevel, double inches, double errorMarginInches, Function<Mode, Double> profileDuration) {
    this.name = name;
    this.isLevel = isLevel;
    this.inches = inches;
    this.errorMarginInches = errorMarginInches;
    this.profileDuration = profileDuration;
  }

  Mode(String name, double inches, double errorMarginInches, Function<Mode, Double> profileDuration) {
    this(name, true, inches, errorMarginInches, profileDuration);
  }

  Mode(String name, double inches, Function<Mode, Double> profileDuration) {
    this(name, true, inches, 0.0, profileDuration);
  }

  Mode(String name) {
    this(name, false, -1, 0.0, null);
  }

  public String getName() {
    return name;
  }

  public boolean isLevel() {
    return isLevel;
  }

  public double getPositionSetpointInches() {
    return inches;
  }

  public int getPositionSetpointTicks() {
    if (encoderTicks == null) {
      encoderTicks = Elevator.calculateTicks(inches);
    }
    return encoderTicks;
  }

  public double getPositionErrorMarginInches() {
    return errorMarginInches;
  }

  public int getPositionErrorTicks() {
    if (errorMarginTicks == null) {
      errorMarginTicks = Elevator.calculateTicks(errorMarginInches);
    }
    return errorMarginTicks;
  }

  public double getDuration(Mode startingMode) {
    return profileDuration.apply(startingMode);
  }
}