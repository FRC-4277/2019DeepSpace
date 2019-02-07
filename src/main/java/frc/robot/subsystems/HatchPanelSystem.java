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
  private boolean hatch = true;

  public HatchPanelSystem(int deviceid) {
    // "0" is the HatchPanelSystem Channel on the PCM (keep it the same)
    grabber = new Solenoid(deviceid, 0);
    arm = new Solenoid(deviceid, 1);
  }

  public void toggleClaw() {
    if (hatch) {
      openClaw();
    } else {
      closeClaw();
    }
  }

  public void toggleArm() {
    if (hatch) {
      extendArm();
    } else {
      retractArm();
    }
  }

  public void retractArm() {
    System.out.println("grabbed");
    //grabber.set(true);
    arm.set(false);
    hatch = true;
  }

  public void extendArm() {
    System.out.println("Released");
    //grabber.set(false);
    arm.set(true);
    hatch = false;
  }

  public void openClaw() {
    grabber.set(false);
    hatch = false;
  }

  public void closeClaw() {
    grabber.set(true);
    hatch = true;
  }

  @Override
  public void initDefaultCommand() {
    openClaw();
  }
}
