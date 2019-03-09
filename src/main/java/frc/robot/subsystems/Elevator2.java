/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.util.function.BiConsumer;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.utils.Settings;
import frc.robot.utils.Settings.Setting;

/**
 * Add your docs here.
 */
public class Elevator2 extends Subsystem {
  /**
   * The timeout for setting config values on the TalonSRX
   */
  private static final int TALONSRX_CONFIGURE_TIMEOUT = 50;
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
  private WPI_TalonSRX mainMotor, followerMotor;
  private Mode runningMode = Mode.MANUAL_CONTROL;
  private Mode reachedMode = Mode.MANUAL_CONTROL;
  // The current mode the PID is configured for
  private Mode pidConfiguredMode = null;
  // The current configuration PID is configured for
  private PIDConfiguration pidConfiguration = null;

  public Elevator2(int talonId, int followerId) {
     super("Elevator");

     // Main Motor
     mainMotor = new WPI_TalonSRX(talonId);
     configureMotorBasics(mainMotor);
     // Set sensor phase
     mainMotor.setSensorPhase(sensorPhaseSetting.getValue());
     // Set position to 0 on bottom limit switch
     mainMotor.configClearPositionOnLimitR(true, TALONSRX_CONFIGURE_TIMEOUT);
     mainMotor.configClearPositionOnLimitF(false, TALONSRX_CONFIGURE_TIMEOUT);
     // Set soft limit for bottom
     mainMotor.configReverseSoftLimitThreshold(0, TALONSRX_CONFIGURE_TIMEOUT);
     mainMotor.configReverseSoftLimitEnable(false, TALONSRX_CONFIGURE_TIMEOUT);

     // TODO : Set soft limits
    
     // Follower Motor
     followerMotor = new WPI_TalonSRX(followerId);
     configureMotorBasics(followerMotor);
     followerMotor.follow(mainMotor);
  }

  public void resetEncoder() {
    mainMotor.setSelectedSensorPosition(0);
  }

  // Manual Drive
  public void drive(double power) {
    runningMode = reachedMode = Mode.MANUAL_CONTROL;
    mainMotor.set(ControlMode.PercentOutput, power);      
  }

  // Manual Stop Drive
  public void stop() {
    drive(0.0);
  }

  // == Target VELOCITY
  public void setTargetVelocity(Mode mode, double velocity) {
    runningMode = mode;
    // Configure PID if necessary
    configurePID(PIDConfiguration.VELOCITY, mode);
    // Set Velocity
    mainMotor.set(ControlMode.Velocity, velocity);
  }

  // == Target POSITION
  public void setTargetPosition(Mode mode, double position) {
    runningMode = mode;
    // Configure PID if necessary
    configurePID(PIDConfiguration.POSITION, mode);
    // Set Position
    mainMotor.set(ControlMode.Position, position);
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
    }
  }

  private void configureVelocityPID() {
    configureEncoder();
    mainMotor.config_kP(0, velocityPSetting.getValue(), TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kI(0, velocityISetting.getValue(), TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kD(0, velocityDSetting.getValue(), TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kF(0, velocityFSetting.getValue(), TALONSRX_CONFIGURE_TIMEOUT);
  }

  private void configurePositionPID(Mode mode) {
    if (!mode.isLevel()) {
      throw new IllegalArgumentException("Mode must be a level");
    }
    configureEncoder();
    mainMotor.config_kP(0, positionPSetting.getValue(), TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kI(0, positionISetting.getValue(), TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kD(0, positionDSetting.getValue(), TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kF(0, positionFSetting.getValue(), TALONSRX_CONFIGURE_TIMEOUT);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
    
  // In inches
  private static final double PVC_DIAMETER = 3.5;
  private static final double PVC_CIRCUMFERENCE = PVC_DIAMETER * Math.PI;
  // (10 is assuming encoder is in middle stage)
  private static final int GEAR_RATIO = 10;
  private static final int ENCODER_TICKS_PER_ROT = 4096;

  public static int calculateTicks(double inches) {
    return (int) Math.round((inches / PVC_CIRCUMFERENCE) * GEAR_RATIO * ENCODER_TICKS_PER_ROT);
  }

  public enum PIDConfiguration {
      VELOCITY((elevator, mode) -> elevator.configureVelocityPID()),
      POSITION(Elevator2::configurePositionPID);

      private BiConsumer<Elevator2, Mode> configurator;
      PIDConfiguration(BiConsumer<Elevator2, Mode> configurator) {
        this.configurator = configurator;
      }

      public void configurePID(Elevator2 elevator, Mode mode) {
        configurator.accept(elevator, mode);
      }
  }

  public enum Mode { 
    MANUAL_CONTROL("Manual Control"),
    HOME("Home", -0.5),
    LOADING_STATION("Loading Station", 16),
    MEDIUM("Medium", 28),
    HIGH("High", 55);

    private String name;
    private boolean isLevel;
    // PID Configuration
    private PIDConfiguration pidConfiguration;
    // Position PID setpoint
    private double inches;
    private Integer encoderTicks;
    // Position PID margin of error
    private double errorMarginInches;
    private Integer errorMarginTicks;

    Mode(String name, boolean isLevel, double inches, double errorMarginInches) {
        this.name = name;
        this.isLevel = isLevel;
        this.inches = inches;
        this.errorMarginInches = errorMarginInches;
    }

    Mode(String name, double inches, double errorMarginInches) {
        this(name, true, inches, errorMarginInches);
    }

    Mode(String name, double inches) {
        this(name, true, inches, 0.0);
    }

    Mode(String name) {
        this(name, false, -1, 0.0);
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
  }
}
