/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * Add your docs here.
 */
public class Elevator extends Subsystem {
  private WPI_TalonSRX mainMotor, followerMotor;
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  public Elevator(int talonId, int followerId) {
    super("Elevator");

    mainMotor = new WPI_TalonSRX(talonId);
    mainMotor.setSubsystem("Elevator");
    mainMotor.setInverted(true);
    mainMotor.setNeutralMode(NeutralMode.Brake);
    mainMotor.setSensorPhase(false);
    resetEncoder();
    stop();    

    //followerMotor = new WPI_TalonSRX(followerId);
    //followerMotor.setSubsystem("Elevator");
    //followerMotor.setInverted(true);
    //followerMotor.follow(mainMotor);
  }

  private void configurePID(double p, double i, double d) {
    mainMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
    mainMotor.configAllowableClosedloopError(0, 1000, 50);
    //mainMotor.configNominal
    mainMotor.config_kP(0, p);
    mainMotor.config_kI(0, i);
    mainMotor.config_kD(0, d);
  }

  public void drive(double power) {
    mainMotor.set(ControlMode.PercentOutput, power);
  }

  public void stop() {
    mainMotor.set(ControlMode.PercentOutput, 0);
  }

  public void goToPosition(double target) {
    configurePID(2, 0, 0);
    System.out.println("goToPosition: " + target);
    mainMotor.set(ControlMode.Position, target);
  }

  public void resetEncoder() {
    mainMotor.setSelectedSensorPosition(0);
  }

  @Override
  public void initDefaultCommand() {
    
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    super.initSendable(builder);
    builder.addDoubleProperty("SensorPosition", new DoubleSupplier(){
    
      @Override
      public double getAsDouble() {
        return mainMotor.getSelectedSensorPosition();
      }
    }, null); 
  }
}
