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

  public Solenoid grabber, arm;
  private boolean hatch;

  public HatchPanelSystem(int deviceid) {
    // "0" is the HatchPanelSystem Channel on the PCM (keep it the same)
    grabber = new Solenoid(deviceid, 0);
    arm = new Solenoid(deviceid, 1);
  }

  public void toggle() {
    if (hatch) {
      releaseHatch();
    } else {
      grabHatch();
    }
  }

  public void grabHatch() {
    System.out.println("grabbed");
    // grabber.set(false);
    // arm.set(true);
    hatch = true;
  }

  public void releaseHatch() {
    System.out.println("Released");
    // solenoid.set(true);
    // arm.set(false);
    hatch = false;
  }

  @Override
  public void initDefaultCommand() {
    grabHatch();
  }
}
