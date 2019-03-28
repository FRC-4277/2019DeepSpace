/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

public class CargoSystem extends Subsystem {

  private Solenoid solenoid;

  public CargoSystem(int deviceid) {
    // "2" is the CargoSystem Channel on the PCM (keep it the same)
    solenoid = new Solenoid(deviceid, 2);
  }

  public void shootBall() {
    solenoid.set(true);
    Timer.delay(0.2);
    solenoid.set(false);
  }

  @Override
  public void initDefaultCommand() {
  }
}
