/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.util.function.Supplier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.commands.ElevatorManualControllerDriveCommand;

/**
 * The subsystem for controlling the elevator. The elevator consists of two
 * TalonSRX's, with one having a CTRE MAG encoder on it.
 */
public class Elevator extends Subsystem {
  /**
   * The allowed error from the target allowed when using PID to go to a position
   */
  private static final int ENCODER_ERROR_ALLOWANCE = 1000;
  /**
   * The timeout for setting config values on the TalonSRX
   */
  private static final int TALONSRX_CONFIGURE_TIMEOUT = 50;
  private WPI_TalonSRX mainMotor, followerMotor;
  /**
   * The current mode the elevator is in
   */
  private Mode mode = Mode.MANUAL_CONTROL;
  /**
   * The PID profile for moving the elevator UP
   */
  private PIDProfile upPID = new PIDProfile(0.014, 0, 0);
  /**
   * The PID profile for moving the elevator DOWN
   */
  private PIDProfile downPID = new PIDProfile(0.008, 0, 0);
  /**
   * Whether the TalonSRX's PID settings are configured for going up. If false,
   * the PID is either unconfigured or configured for down.
   */
  private boolean talonConfiguredForUp = false;

  public Elevator(int talonId, int followerId) {
    super("Elevator");

    mainMotor = new WPI_TalonSRX(talonId);
    configureMotorBasics(mainMotor);
    // Change sensor phase depending on if clone or competition
    mainMotor.setSensorPhase(true);
    /*
     * UNCOMMENT WHEN LIMIT SWITCHES ARE INSTALLED // Clear position when reverse
     * limit switch is activated mainMotor.configClearPositionOnLimitR(true,
     * TALONSRX_CONFIGURE_TIMEOUT); // Don't clear position when forward (top) limit
     * switch is activated, other code stops motor
     * mainMotor.configClearPositionOnLimitF(false, TALONSRX_CONFIGURE_TIMEOUT);
     * 
     */
    // Set soft limit for bottom
    mainMotor.configReverseSoftLimitThreshold(0, TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.configReverseSoftLimitEnable(true, TALONSRX_CONFIGURE_TIMEOUT);
    // Set soft limit for top to 2 inches more than HIGH setpoint
    // 22351 = (2 / 3.5PI) * 30 * 4096
    // See comments below in Mode.MEDIUM for calculation explanation
    mainMotor.configForwardSoftLimitThreshold(Mode.HIGH.getSetPoint() + 22351, TALONSRX_CONFIGURE_TIMEOUT);

    followerMotor = new WPI_TalonSRX(followerId);
    configureMotorBasics(followerMotor);
    followerMotor.follow(mainMotor);

    resetEncoder();
    stop();
  }

  private void configureMotorBasics(WPI_TalonSRX talonSRX) {
    talonSRX.setSubsystem("Elevator");
    talonSRX.setInverted(true);
    talonSRX.setNeutralMode(NeutralMode.Brake);
  }

  private void configurePID(PIDProfile profile) {
    configurePID(profile.p, profile.i, profile.d, profile.f);
  }

  private void configurePID(double p, double i, double d, double f) {
    mainMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.configAllowableClosedloopError(0, ENCODER_ERROR_ALLOWANCE, TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kP(0, p, TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kI(0, i, TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kD(0, d, TALONSRX_CONFIGURE_TIMEOUT);
    mainMotor.config_kF(0, f, TALONSRX_CONFIGURE_TIMEOUT);
  }

  private boolean checkTopSwitch() {
    // TEMPORARY UNTIL LIMIT SWITCHES INSTALLED
    return true;

    /*
     * boolean closed = mainMotor.getSensorCollection().isFwdLimitSwitchClosed(); if
     * (closed) { // The top limit switch is closed, set output to 0%
     * mainMotor.set(ControlMode.PercentOutput, 0); } return closed;
     */
  }

  /**
   * Drive elevator manually.
   * 
   * @param power Power to set motor to (between -1.0 and 1.0 inclusive)
   */
  public void drive(double power) {
    mode = Mode.MANUAL_CONTROL;
    // If top switch is triggered, we must not move
    if (!checkTopSwitch()) {
      return;
    }
    mainMotor.set(ControlMode.PercentOutput, power);
  }

  /**
   * Stop elevator manually (set motor power to 0.0).
   */
  public void stop() {
    mode = Mode.MANUAL_CONTROL;
    mainMotor.set(ControlMode.PercentOutput, 0);
  }

  /**
   * Go to position manually (call repetitively to approach target)
   * 
   * @param target Position to approach with PID loop (in encoder ticks)
   */
  public void goToPosition(double target) {
    mode = Mode.MANUAL_TARGET;
    // If top switch is triggered, we must not move
    if (!checkTopSwitch()) {
      return;
    }
    mainMotor.set(ControlMode.Position, target);
  }

  /**
   * Go to a specified mode. If this mode is a level, then it will configure the
   * Talon's PID automatically and go there. (call with this mode repetitively to
   * approach target) If the mode is not a level (e.g MANUAL_CONTROL or
   * MANUAL_TARGET mode), then it will just update the current mode.
   * 
   * @param mode the mode to set (and if it's a level, go to)
   */
  public void goToMode(Mode mode) {
    this.mode = mode;
    // Check if this mode is an elevator level. The only modes that aren't an
    // elevator level are MANUAL_CONTROL and MANUAL_TARGET
    if (mode.isElevatorLevel()) {
      // If top switch is triggered, we must not move
      if (!checkTopSwitch()) {
        return;
      }

      boolean up = mode.getSetPoint() > getSensorPosition();
      // If we need to go UP and we're NOT configured for up, let's configure for UP
      if (up && !talonConfiguredForUp) {
        // Configure PID for UP
        configurePID(upPID);
        talonConfiguredForUp = true;
        // If we need to go DOWN and we're configured for UP, let's configure for DOWN
      } else if (!up && talonConfiguredForUp) {
        // Configure PID for DOWN
        configurePID(downPID);
        talonConfiguredForUp = false;
      }
      // Tell TalonSRX to go to Setpoint of mode
      mainMotor.set(ControlMode.Position, mode.getSetPoint());
    }
  }

  public Mode getMode() {
    return mode;
  }

  /**
   * Reset the TalonSRX's MAG encoder to {@code 0}
   */
  public void resetEncoder() {
    mainMotor.setSelectedSensorPosition(0);
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new ElevatorManualControllerDriveCommand());
  }

  public int getSensorPosition() {
    return mainMotor.getSelectedSensorPosition();
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    super.initSendable(builder);
    builder.addDoubleProperty("SensorPosition", this::getSensorPosition, null);
    builder.addStringProperty("Mode", new Supplier<String>() {
      public String get() {
        return mode.getName();
      }
    }, null);
  }

  /**
   * Represents the constants in a PIDF closed loop
   */
  public class PIDProfile {
    public double p, i, d, f;

    public PIDProfile(double p, double i, double d) {
      this(p, i, d, 0);
    }

    /**
     * Construct a PIDProfile
     * 
     * @param p Proportional Gain (kP)
     * @param i Integral Gain (kI)
     * @param d Derivative Gain (kD)
     * @param f Feed-Forward (kF)
     */
    public PIDProfile(double p, double i, double d, double f) {
      this.p = p;
      this.i = i;
      this.d = d;
      this.f = f;
    }
  }

  /**
   * Represents the states of the elevator (manually driven or trying to stay at a
   * certain level).
   */
  public enum Mode {
    /**
     * This mode is when the elevator is being controlled manually (through
     * controller)
     */
    MANUAL_CONTROL("Manual Control"),
    /**
     * This mode is when a manual value is set for the target position
     */
    MANUAL_TARGET("Manual Target"),
    /**
     * Home level, where the elevator starts and to score at lowest hatches and
     * cargo on rocket.
     */
    HOME("Home", 0),
    /**
     * Level at loading station.
     */
    LOADING_STATION("Loading Station", 201155),
    /**
     * Level at medium ports on the rocket
     * <p>
     * Encoder Ticks Calculation w/ Explanation (math can be applied to other
     * positions):
     * 
     * The elevator's PVC pipe has a diameter of 3.5", so the circumference of it is
     * about 11". Therefore one rotation of the PVC moves the elevator 11". The
     * travel distance to MEDIUM is 28", so dividing 28" by 11", we'll need ~2.54
     * rotations of the PVC. The gear box makes it so 30 turns of the motor is 1
     * turn of the PVC (30:1 gear ratio), so multiply 2.54 by 30 to get about 76
     * motor rotations needed. Lastly, the CTRE MAG Encoder's position increases by
     * 4096 per rotation, so multiply 76 by 4096 to get the encoder position needed
     * in terms of ticks: {@code 4096 * 76 = about 312,000 encoder ticks}.
     * 
     * The formula is
     * {@code Encoder Ticks = (Travel Distance / (3.5 * PI) ) * 30 * 4096}.
     */
    MEDIUM("Medium", 312935),
    /**
     * Level at highest ports on the rocket
     */
    HIGH("High", 614647);

    private String name;
    private boolean isElevatorLevel;
    private int setPoint;

    private Mode(String name) {
      this.name = name;
      this.isElevatorLevel = false;
      this.setPoint = -1;
    }

    private Mode(String name, int setPoint) {
      this.name = name;
      this.setPoint = setPoint;
      this.isElevatorLevel = true;
    }

    public String getName() {
      return name;
    }

    public int getSetPoint() {
      return setPoint;
    }

    public boolean isElevatorLevel() {
      return isElevatorLevel;
    }
  }
}
