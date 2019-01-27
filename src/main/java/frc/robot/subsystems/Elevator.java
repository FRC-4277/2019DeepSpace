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

/**
 * The subsystem for controlling the elevator. 
 * The elevator consists of two TalonSRX's,
 * with one having a CTRE MAG encoder on it.
 */
public class Elevator extends Subsystem {
  /**
   * The allowed error from the target allowed
   * when using PID to go to a position
   */
  private static final int ENCODER_PID_ERROR_ALLOWANCE = 1000;
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
   * Whether the TalonSRX's PID settings are configured for going up.
   * If false, the PID is either unconfigured or configured for down.
   */
  private boolean talonConfiguredForUp = false;

  public Elevator(int talonId, int followerId) {
    super("Elevator");

    mainMotor = new WPI_TalonSRX(talonId);
    mainMotor.setSubsystem("Elevator");
    mainMotor.setInverted(true);
    mainMotor.setNeutralMode(NeutralMode.Brake);
    mainMotor.setSensorPhase(true);
    
    followerMotor = new WPI_TalonSRX(followerId);
    followerMotor.setSubsystem("Elevator");
    followerMotor.setInverted(true);
    followerMotor.setNeutralMode(NeutralMode.Brake);
    followerMotor.follow(mainMotor);

    resetEncoder();
    stop();
  }

  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  private void configurePID(PIDProfile profile) {
    configurePID(profile.p, profile.i, profile.d, profile.f);
  }

  private void configurePID(double p, double i, double d, double f) {
    mainMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 50);
    mainMotor.configAllowableClosedloopError(0, ENCODER_PID_ERROR_ALLOWANCE, 50);
    mainMotor.config_kP(0, p, 50);
    mainMotor.config_kI(0, i, 50);
    mainMotor.config_kD(0, d, 50);
    mainMotor.config_kF(0, f, 50);
  }

  /**
   * Drive elevator manually.
   * @param power power to set motor to (between -1.0 and 1.0 inclusive)
   */
  public void drive(double power) {
    mode = Mode.MANUAL_CONTROL;
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
   * Go to position manually
   * @param target target position (in encoder ticks)
   */
  public void goToPosition(double target) {
    mainMotor.set(ControlMode.Position, target);
  }

  /**
   * Go to a specified mode. If this mode is a level, then it will configure the Talon's PID automatically and go there.
   * (call with this mode repetitively to approach target)
   * If the mode is not a level (e.g MANUAL mode), then it will just update the current mode.
   * @param mode the mode to set (and if it's a level, go to)
   */
  public void goToMode(Mode mode) {
    this.mode = mode;
    // Check if this mode is an elevator level. The only mode that isn't an elevator mode is MANUAL
    if (mode.isElevatorLevel()) {
      boolean up = mode.getSetPoint() > getSensorPosition();
      // If we need to go UP and we're NOT configured for up, let's configure for UP
      if (up && !talonConfiguredForUp) {
        // Configure PID for UP
        configurePID(upPID);
        talonConfiguredForUp = true;
      // If we need to go DOWN and we're configured for UP, let's configure for DOWN
      } else if (!up && talonConfiguredForUp){
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
        return mode.name();
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
   * Represents the states of the elevator (manually driven or trying to stay at a certain level).
   */
  public enum Mode {
    /**
     * This mode is when the elevator is being controlled manually (through controller)
     */
    MANUAL_CONTROL(false),
    /**
     * Home level, where the elevator starts and to score at lowest hatches and cargo on rocket.
     */
    HOME(0, true),
    /**
     * Level at loading station.
     */
    LOADING_STATION(201_155, true),
    /**
     * Level at medium ports on the rocket
     */
    MEDIUM(312_935, true),
    /**
     * Level at highest ports on the rocket
     */
    HIGH(614_647, true);

    private boolean isElevatorLevel;
    private int setPoint;

    private Mode(boolean isElevatorLevel) {
      this(-1, isElevatorLevel);
    }

    private Mode(int setPoint, boolean isElevatorLevel) {
      this.setPoint = setPoint;
      this.isElevatorLevel = isElevatorLevel;
    }

    public int getSetPoint() {
      return setPoint;
    }

    public boolean isElevatorLevel() {
      return isElevatorLevel;
    }
  }
}
