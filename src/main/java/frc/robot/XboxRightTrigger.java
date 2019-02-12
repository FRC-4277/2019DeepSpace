/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Trigger;

/**
 * Add your docs here.
 */
public class XboxRightTrigger extends Trigger {
  private XboxController xboxController;

  public XboxRightTrigger(XboxController xboxController) {
    this.xboxController = xboxController;
  }
  
  @Override
  public boolean get() {
    return xboxController.getTriggerAxis(Hand.kRight) > 0.1;
  }
}
