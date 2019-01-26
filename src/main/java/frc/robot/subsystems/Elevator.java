/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * Add your docs here.
 */
public class Elevator extends Subsystem {
  private static final int ENCODER_PID_ERROR_ALLOWANCE = 1000;
  private WPI_TalonSRX mainMotor, followerMotor;
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  private Mode mode = Mode.MANUAL_CONTROL;
  private PIDProfile upPID = new PIDProfile(0.014, 0, 0);
  private PIDProfile downPID = new PIDProfile(0, 0, 0);

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
    followerMotor.follow(mainMotor);

    resetEncoder();
    stop();
  }

  private void configurePID(PIDProfile profile) {
    configurePID(profile.p, profile.i, profile.d, profile.f);
  }

  private void configurePID(double p, double i, double d, double f) {
    mainMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 50);
    mainMotor.configAllowableClosedloopError(0, ENCODER_PID_ERROR_ALLOWANCE, 50);
    //mainMotor.configNominal
    mainMotor.config_kP(0, p, 50);
    mainMotor.config_kI(0, i, 50);
    mainMotor.config_kD(0, d, 50);
    mainMotor.config_kF(0, f, 50);
  }

  public void drive(double power) {
    mode = Mode.MANUAL_CONTROL;
    mainMotor.set(ControlMode.PercentOutput, power);
  }

  public void stop() {
    mode = Mode.MANUAL_CONTROL;
    mainMotor.set(ControlMode.PercentOutput, 0);
  }

  public void goToPosition(double target) {
    System.out.println("goToPosition: " + target);
    mainMotor.set(ControlMode.Position, target);
  }

  public void goToModeIndefinitely(Mode mode) {
    if (mode.isElevatorLevel()) {
      boolean up = mode.getSetPoint() > getSensorPosition();
      if (up) {
        // Configure PID for UP
        configurePID(upPID);
      } else {
        // Configure PID for DOWN
        configurePID(downPID);
      }
      mainMotor.set(ControlMode.Position, mode.getSetPoint());
    }
  }

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
  }

  public class PIDProfile {
    public double p, i, d, f;

    public PIDProfile(double p, double i, double d) {
      this(p, i, d, 0);
    }

    public PIDProfile(double p, double i, double d, double f) {
      this.p = p;
      this.i = i;
      this.d = d;
      this.f = f;
    }
  }

  public enum Mode {
    // MANUAL CONTROL
    MANUAL_CONTROL(false),
    // LEVELS
    HOME(0, true),
    LOADING_STATION(50_000, true),
    LOW(100_000, true),
    MEDIUM(312_935, true),
    HIGH(200_000, true);

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
