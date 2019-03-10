/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.commands.ElevatorManualControllerDriveCommand;
import frc.robot.utils.Settings;
import frc.robot.utils.Settings.Setting;

/**
 * Add your docs here.
 */
public class Elevator extends Subsystem {
  /**
   * The timeout for setting config values on the TalonSRX
   */
  private static final int TALONSRX_CONFIGURE_TIMEOUT = 50;

  //<editor-fold desc="Settings">
  private Setting<Boolean> sensorPhaseSetting = Settings
    .createToggleSwitch("Invert Drive", true)
    .defaultValue(true)
    .build();
  // Velocity PID settings
  private Setting<Double> velocityPSetting = Settings
    .createDoubleField("Velocity P", true)
    .defaultValue(0.08) // TODO : Change to known working
    .build();
  private Setting<Double> velocityISetting = Settings
    .createDoubleField("Velocity I", true)
    .defaultValue(0.0) // TODO : Change
    .build();
  private Setting<Double> velocityDSetting = Settings
    .createDoubleField("Velocity D", true)
    .defaultValue(0.0) // TODO : Change
    .build();
  private Setting<Double> velocityFSetting = Settings
    .createDoubleField("Velocity F", true)
    .defaultValue(0.0) // TODO : Change
    .build();
    // Position PID settings
  private Setting<Double> positionPSetting = Settings
    .createDoubleField("Position P", true)
    .defaultValue(0.08) // TODO : Change to known working
    .build();
  private Setting<Double> positionISetting = Settings
    .createDoubleField("Position I", true)
    .defaultValue(0.0) // TODO : Change
    .build();
  private Setting<Double> positionDSetting = Settings
    .createDoubleField("Position D", true)
    .defaultValue(0.0) // TODO : Change
    .build();
  private Setting<Double> positionFSetting = Settings
    .createDoubleField("Position F", true)
    .defaultValue(0.0) // TODO : Change
    .build();
  //</editor-fold>

  private WPI_TalonSRX mainMotor, followerMotor;
  private Mode runningMode = Mode.MANUAL_CONTROL;
  private Mode reachedMode = Mode.MANUAL_CONTROL;
  // The current mode the PID is configured for
  private Mode pidConfiguredMode = null;
  // The current configuration PID is configured for
  private PIDConfiguration pidConfiguration = null;

  // ShuffleBoard
  private Mode lastRunningModeInEntry = Mode.MANUAL_CONTROL;
  private Mode lastReachedModeInEntry = Mode.MANUAL_CONTROL;
  private PIDConfiguration lastPIDConfigInEntry = null;
  private NetworkTableEntry runningModeEntry, reachedModeEntry, pidConfigEntry, velocityEntry, positionEntry;

  public Elevator(int talonId, int followerId) {
     super("Elevator");

     // Main Motor
     mainMotor = new WPI_TalonSRX(talonId);
     configureMotorBasics(mainMotor);
     // Set sensor phase
     mainMotor.setSensorPhase(sensorPhaseSetting.getValue());
     // Set position to 0 on bottom limit switch
     mainMotor.configClearPositionOnLimitR(true, TALONSRX_CONFIGURE_TIMEOUT);
     // *Don't* 0 position at top
     mainMotor.configClearPositionOnLimitF(false, TALONSRX_CONFIGURE_TIMEOUT);
     // Set soft limit for bottom
     mainMotor.configReverseSoftLimitThreshold(0, TALONSRX_CONFIGURE_TIMEOUT);
     mainMotor.configReverseSoftLimitEnable(false, TALONSRX_CONFIGURE_TIMEOUT);

     // TODO : Set soft limit for top
    
     // Follower Motor
     followerMotor = new WPI_TalonSRX(followerId);
     configureMotorBasics(followerMotor);
     followerMotor.follow(mainMotor);

     // Reset everything
     stop();
     resetEncoder();

    /* Add Elevator Modes to Shuffleboard */
    runningModeEntry = Shuffleboard.getTab("General")
      .add("Running Mode", runningMode.name())
      .withWidget(BuiltInWidgets.kTextView)
      // POSITION & SIZE
      .withPosition(6, 0)
      .withSize(1, 1)
      .getEntry();
    reachedModeEntry = Shuffleboard.getTab("General")
      .add("Reached Mode", reachedMode.name())
      .withWidget(BuiltInWidgets.kTextView)
      // POSITION & SIZE
      .withPosition(7, 0)
      .withSize(1, 1)
      .getEntry();

    /* Add PID Config to Shuffleboard */
    pidConfigEntry = Shuffleboard.getTab("General")
      .add("PID Config", "None")
      .withWidget(BuiltInWidgets.kTextView)
      // POSITION & SIZE
      .withPosition(8, 0)
      .withSize(1, 1)
      .getEntry();

    /* Add Elevator Sensor Info to Shuffleboard */
    velocityEntry = Shuffleboard.getTab("General")
      .add("Elevator Velocity", "0 = 0\"")
      .withPosition(6, 1)
      .withSize(1, 1)
      .getEntry();
    positionEntry = Shuffleboard.getTab("General")
      .add("Elevator Position", "0 = 0\"")
      .withPosition(7, 1)
      .withSize(1, 1)
      .getEntry();
  }

  @Override
  public void periodic() {
    int velocity = getVelocityTicks();
    velocityEntry.setString(velocity + " = " + calculateInches(velocity) + "\"");
    int position = getEncoderTicks();
    positionEntry.setString(position + " = " + calculateInches(position) + "\"");
  }

  public void resetEncoder() {
    mainMotor.setSelectedSensorPosition(0);
  }

  public int getVelocityTicks() {
    return mainMotor.getSelectedSensorVelocity();
  }

  public int getEncoderTicks() {
      return mainMotor.getSelectedSensorPosition();
  }

  public double getHeightInches() {
    return calculateInches(getEncoderTicks());
  }

  // Manual Drive
  public void drive(double power) {
    setRunningMode(Mode.MANUAL_CONTROL);
    setReachedMode(Mode.MANUAL_CONTROL);
    mainMotor.set(ControlMode.PercentOutput, power);      
  }

  // Manual Stop Drive
  public void stop() {
    drive(0.0);
  }

  // == Target VELOCITY
  public void setTargetVelocity(Mode mode, double velocity) {
    setRunningMode(mode);
    // Configure PID if necessary
    configurePID(PIDConfiguration.VELOCITY, mode);
    // Set Velocity
    mainMotor.set(ControlMode.Velocity, velocity);
  }

  // == Target POSITION
  public void setTargetPosition(Mode mode, double position) {
    setReachedMode(mode);
    // Configure PID if necessary
    configurePID(PIDConfiguration.POSITION, mode);
    // Set Position
    mainMotor.set(ControlMode.Position, position);
  }

  /**
   * Check whether the elevator has reached a mode
   * @param mode The mode to check if the elevator is at
   * @param setReachedMode Whether to update the reached mode {@see getReachedMode()} if it has reached this mode
   * @return Whether the elevator has reached the specified mode
   */
  public boolean hasReachedMode(Mode mode, boolean setReachedMode) {
      if (hasReachedPosition(mode.getPositionSetpointTicks(), mode.getPositionErrorTicks())) {
          // Reached, within margin of error
          if (setReachedMode) {
            setReachedMode(mode);
          }
          return true;
      }
      return false;
  }

  public boolean hasReachedPosition(int ticks, int errorMargin) {
    return Math.abs(getEncoderTicks() - ticks) <= errorMargin;
  }

  public boolean hasReachedPositionInches(double inches, double errorMarginInches) {
    return hasReachedPosition(calculateTicks(inches), calculateTicks(errorMarginInches));
  }

  private void setRunningMode(Mode mode) {
    this.runningMode = mode;
    if (lastRunningModeInEntry != mode) {
      runningModeEntry.setString(mode.name());
      lastReachedModeInEntry = mode;
    }
  }

  public Mode getRunningMode() {
    return runningMode;
  }

  private void setReachedMode(Mode mode) {
    this.reachedMode = mode;
    if (lastReachedModeInEntry != mode) {
      reachedModeEntry.setString(mode.name);
      lastReachedModeInEntry = mode;
    }
  }

  public Mode getReachedMode() {
    return reachedMode;
  }

  private void configureMotorBasics(WPI_TalonSRX talonSRX) {
    talonSRX.setSubsystem("Elevator");
    talonSRX.configFactoryDefault();
    talonSRX.setInverted(true);
    talonSRX.setNeutralMode(NeutralMode.Brake);
  }

  private void configureEncoder() {
    mainMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, TALONSRX_CONFIGURE_TIMEOUT);
  }

  private void configurePID(PIDConfiguration config, Mode mode) {
    if (this.pidConfiguredMode != mode || this.pidConfiguration != config) {
      // Configure
      config.configurePID(this, mode);
      // Update fields
      this.pidConfiguredMode = mode;
      this.pidConfiguration = config;
      // Update Shuffleboard
      if (lastPIDConfigInEntry != config) {
        pidConfigEntry.setString(config.name());
        lastPIDConfigInEntry = config;
      }
    }
  }

  private void configureVelocityPID() {
    configureEncoder();
    configurePID(velocityPSetting, velocityISetting, velocityDSetting, velocityFSetting);
  }

  private void configurePositionPID(Mode mode) {
    if (!mode.isLevel()) {
      throw new IllegalArgumentException("Mode must be a level");
    }
    configureEncoder();
    mainMotor.configAllowableClosedloopError(0, mode.getPositionErrorTicks(), TALONSRX_CONFIGURE_TIMEOUT);
    configurePID(positionPSetting, positionISetting, positionDSetting, positionFSetting);
  }

  private void configurePID(Setting<Double> p, Setting<Double> i, Setting<Double> d, Setting<Double> f) {
    mainMotor.config_kP(0, p.getValue(), TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kI(0, i.getValue(), TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kD(0, d.getValue(), TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kF(0, f.getValue(), TALONSRX_CONFIGURE_TIMEOUT);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
    setDefaultCommand(new ElevatorManualControllerDriveCommand());
  }
    
  // In inches
  private static final double PVC_DIAMETER = 3.5;
  private static final double PVC_CIRCUMFERENCE = PVC_DIAMETER * Math.PI;
  // (10 is assuming encoder is in middle stage)
  private static final int GEAR_RATIO = 10;
  private static final int ENCODER_TICKS_PER_ROTATION = 4096;

  public static int calculateTicks(double inches) {
    return (int) Math.round((inches / PVC_CIRCUMFERENCE) * GEAR_RATIO * ENCODER_TICKS_PER_ROTATION);
  }

  public static double calculateInches(int ticks) {
    return (((double) ticks / GEAR_RATIO) / ENCODER_TICKS_PER_ROTATION) * (PVC_CIRCUMFERENCE);
  }

  public enum PIDConfiguration {
      VELOCITY((elevator, mode) -> elevator.configureVelocityPID()),
      POSITION(Elevator::configurePositionPID);

      private BiConsumer<Elevator, Mode> configurator;
      PIDConfiguration(BiConsumer<Elevator, Mode> configurator) {
        this.configurator = configurator;
      }

      public void configurePID(Elevator elevator, Mode mode) {
        configurator.accept(elevator, mode);
      }
  }

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
    HOME("Home", -0.75,
            (mode) -> mode.name().equals("HOME") ? 0.25 : (mode.getDuration(Mode.valueOf("HOME")) * 1.3)),
    /**
     * Level where we're shooting cargo into cargo ship or taking a cargo from loading station
     */
    LOADING_STATION("Loading Station", 16, (mode) -> 0.7),
    /**
     * Level where we're placing cargo or hatches on rocket middle ports
     */
    MEDIUM("Medium", 28, (mode) -> 1.1),
    /**
     * Level where we're placing cargo or hatches on rocket high ports
     */
    HIGH("High", 55, (mode) -> 1.5);

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
            encoderTicks = calculateTicks(inches);
        }
        return encoderTicks;
    }

    public double getPositionErrorMarginInches() {
        return errorMarginInches;
    }

    public int getPositionErrorTicks() {
        if (errorMarginTicks == null) {
            errorMarginTicks = calculateTicks(errorMarginInches);
        }
        return errorMarginTicks;
    }

    public double getDuration(Mode startingMode) {
      return profileDuration.apply(startingMode);
    }
  }
}
