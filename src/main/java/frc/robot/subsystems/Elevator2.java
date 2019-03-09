/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

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
  private Setting<Integer> velocityPSetting = Settings
    .createIntField("Velocity P", true)
    .defaultValue(0.08) // TODO : Change to known working
    .build();
  private Setting<Integer> velocityPSetting = Settings
    .createIntField("Velocity I", true)
    .defaultValue(0.0) // TODO : Change
    .build();
  private Setting<Integer> velocityPSetting = Settings
    .createIntField("Velocity D", true)
    .defaultValue(0.0) // TODO : Change
    .build();
  private Setting<Integer> velocityPSetting = Settings
    .createIntField("Velocity F", true)
    .defaultValue(0.0) // TODO : Change
    .build();
  private WPI_TalonSRX mainMotor, followerMotor;

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

  private void configureMotorBasics(WPI_TalonSRX talonSRX) {
    talonSRX.setSubsystem("Elevator");
    talonSRX.configFactoryDefault();
    talonSRX.setInverted(true);
    talonSRX.setNeutralMode(NeutralMode.Brake);
  }

  public void resetEncoder() {
    mainMotor.setSelectedSensorPosition(0);
  }

  private void configureVelocityPID() {
      talonSRX.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, TALONSRX_CONFIGURE_TIMEOUT);
      talonSRX.configAllowableClosedloopError(0, ENCODER_ERROR_ALLOWANCE, )
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
