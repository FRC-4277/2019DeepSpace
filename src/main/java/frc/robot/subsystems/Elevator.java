/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Add your docs here.
 */
public class Elevator extends Subsystem {
  private WPI_TalonSRX mainMotor;
  private WPI_TalonSRX followerMotor;
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  public Elevator(int talonId) {
    super("Elevator");

    mainMotor = new WPI_TalonSRX(talonId);
    mainMotor.setSubsystem("Elevator");
    mainMotor.setNeutralMode(NeutralMode.Brake);
    mainMotor.setSensorPhase(false);
    mainMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
    mainMotor.config_kP(0, 0.01);
    mainMotor.config_kI(0, 0);
    mainMotor.config_kD(0, 0);
    mainMotor.config_kF(0, 0);

    followerMotor.follow(mainMotor);
  }

  public void drive(double power) {
    mainMotor.disable();
    mainMotor.set(power);
  }

  public void stop() {
    mainMotor.set(0);
  }

  public void goToPosition(double target) {
    mainMotor.set(ControlMode.Position, target);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
