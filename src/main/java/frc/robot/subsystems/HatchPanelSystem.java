/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Add your docs here.
 */
public class HatchPanelSystem extends Subsystem {

  public Solenoid solenoid;

  public HatchPanelSystem(int deviceid) {
    // "0" is the HatchPanelSystem Channel (keep it the same)
    solenoid = new Solenoid(deviceid, 0);
  }

  public void grabHatch() {
    System.out.println("grabbed");
    // solenoid.set(true);
  }

  public void releaseHatch() {
    System.out.println("Released");
    // solenoid.set(false);
  }

  @Override
  public void initDefaultCommand() {
    grabHatch();
  }
}
