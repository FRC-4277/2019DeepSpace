/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems.elevator;

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
  // In inches
  private static final double PVC_DIAMETER = 3.5;
  private static final double PVC_CIRCUMFERENCE = PVC_DIAMETER * Math.PI;
  // Gear ratio from encoder to elevator
  private static final int GEAR_RATIO = 3;
  private static final int ENCODER_TICKS_PER_ROTATION = 4096;

  /**
   * The timeout for setting config values on the TalonSRX
   */
  private static final int TALONSRX_CONFIGURE_TIMEOUT = 50;

  //<editor-fold desc="Settings">
  private Setting<Boolean> sensorPhaseSetting;
  // Velocity PID settings
  private Setting<Double> velocityPSetting;
  private Setting<Double> velocityISetting;
  private Setting<Double> velocityDSetting;
  private Setting<Double> velocityFSetting;
  // Position PID settings
  private Setting<Double> positionPSetting;
  private Setting<Double> positionISetting;
  private Setting<Double> positionDSetting;
  private Setting<Double> positionFSetting;
  //</editor-fold>

  private WPI_TalonSRX mainMotor, followerMotor;
  private Mode runningMode = Mode.MANUAL_CONTROL;
  private Mode reachedMode = Mode.MANUAL_CONTROL;
  // The current mode the PID is configured for
  private Mode pidConfiguredMode = null;
  // The current configuration PID is configured for
  private PIDConfiguration pidConfiguration = null;

  // ShuffleBoard
  private Mode lastEntryRunningMode = Mode.MANUAL_CONTROL;
  private Mode lastEntryReachedMode = Mode.MANUAL_CONTROL;
  private PIDConfiguration lastPIDConfigInEntry = null;
  private NetworkTableEntry runningModeEntry, reachedModeEntry, pidConfigEntry, velocityEntry, positionEntry;

  public static int calculateTicks(double inches) {
    return (int) Math.round((inches / PVC_CIRCUMFERENCE) * GEAR_RATIO * ENCODER_TICKS_PER_ROTATION);
  }

  public static double calculateInches(int ticks) {
    return (((double) ticks) / (GEAR_RATIO * ENCODER_TICKS_PER_ROTATION)) * (PVC_CIRCUMFERENCE);
  }

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

     // Set top soft limit to 4" above top levle
     mainMotor.configForwardSoftLimitThreshold(Mode.HIGH.getPositionSetpointTicks() + calculateTicks(4.0), TALONSRX_CONFIGURE_TIMEOUT);
     mainMotor.configForwardSoftLimitEnable(true, TALONSRX_CONFIGURE_TIMEOUT);

     // Follower Motor
     followerMotor = new WPI_TalonSRX(followerId);
     configureMotorBasics(followerMotor);
     followerMotor.follow(mainMotor);

     // Reset everything
     stop();
     resetEncoder();

    addShuffleboardEntries();

    // TODO : Move settings and shuffleboard to private methods
  }

  private void addShuffleboardEntries() {
    sensorPhaseSetting = Settings
      .createToggleSwitch("Sensor Phase", true)
      .defaultValue(true)
      .build();
    velocityPSetting = Settings
      .createDoubleField("Velocity P", true)
      .defaultValue(0.0000330815)
      .build();
    velocityISetting = Settings
      .createDoubleField("Velocity I", true)
      .defaultValue(0.0)
      .build();
    velocityDSetting = Settings
      .createDoubleField("Velocity D", true)
      .defaultValue(0.0)
      .build();
    velocityFSetting = Settings
      .createDoubleField("Velocity F", true)
      .defaultValue(0.0)
      .build();
    positionPSetting = Settings
      .createDoubleField("Position P", true)
      .defaultValue(0.0000330815)
      .build();
    positionISetting = Settings
      .createDoubleField("Position I", true)
      .defaultValue(0.0)
      .build();
    positionDSetting = Settings
      .createDoubleField("Position D", true)
      .defaultValue(0.0)
      .build();
    positionFSetting = Settings
      .createDoubleField("Position F", true)
      .defaultValue(0.0)
      .build();
    

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
  // TODO : Change manual stop to 0 PID
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
    setRunningMode(mode);
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
    if (lastEntryRunningMode != mode) {
      runningModeEntry.setString(mode.getName());
      lastEntryRunningMode = mode;
    }
  }

  public Mode getRunningMode() {
    return runningMode;
  }

  private void setReachedMode(Mode mode) {
    this.reachedMode = mode;
    if (lastEntryReachedMode != mode) {
      reachedModeEntry.setString(mode.getName());
      lastEntryReachedMode = mode;
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

  public void configureVelocityPID() {
    configureEncoder();
    configurePID(velocityPSetting, velocityISetting, velocityDSetting, velocityFSetting);
  }

  public void configurePositionPID(Mode mode) {
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
    // TODO : Change?
    setDefaultCommand(new ElevatorManualControllerDriveCommand());
  }

}
