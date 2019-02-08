/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.ElevatorManualControllerDriveCommand;
import frc.robot.commands.ElevatorMoveToHighCommand;
import frc.robot.commands.ElevatorMoveToHomeCommand;
import frc.robot.commands.ElevatorMoveToLoadingStationCommand;
import frc.robot.commands.ElevatorMoveToMediumCommand;
import frc.robot.commands.ElevatorResetEncoderCommand;
import frc.robot.commands.HatchPanelArmToggle;
import frc.robot.commands.CargoShootCommand;
import frc.robot.commands.HatchPanelGrabToggle;
import frc.robot.commands.hatchcommandgroup.HatchLoadingStationGroup;
import frc.robot.commands.hatchcommandgroup.HatchRocketPlaceGroup;
import frc.robot.map.XboxControllerMap;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
  //// CREATING BUTTONS
  // One type of button is a joystick button which is any button on a
  //// joystick.
  // You create one by telling it which joystick it's on and which button
  // number it is.
  // Joystick stick = new Joystick(port);
  // Button button = new JoystickButton(stick, buttonNumber);

  // There are a few additional built in buttons you can use. Additionally,
  // by subclassing Button you can create custom triggers and bind those to
  // commands the same as any other Button.

  //// TRIGGERING COMMANDS WITH BUTTONS
  // Once you have a button, it's trivial to bind it to a button in one of
  // three ways:

  // Start the command when the button is pressed and let it run the command
  // until it is finished as determined by it's isFinished method.
  // button.whenPressed(new ExampleCommand());

  // Run the command while the button is being held down and interrupt it once
  // the button is released.
  // button.whileHeld(new ExampleCommand());

  // Start the command when the button is released and let it run the command
  // until it is finished as determined by it's isFinished method.
  // button.whenReleased(new ExampleCommand());

  public static Joystick driveStick = new Joystick(0);
  // Normal co-pilot controller
  public static Joystick xboxController1 = new Joystick(1);
  // Manual co-pilot controller
  public static Joystick xboxController2 = new Joystick(2);

  static {

    // NORMAL CO-PILOT CONTROLS

    Button buttonA = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_A);
    buttonA.whenPressed(new ElevatorMoveToHomeCommand(true));

    Button buttonB = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_B);
    buttonB.whenPressed(new ElevatorMoveToLoadingStationCommand(true));

    Button buttonX = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_X);
    buttonX.whenPressed(new ElevatorMoveToMediumCommand(true));

    Button buttonY = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_Y);
    buttonY.whenPressed(new ElevatorMoveToHighCommand(true));

    Button buttonBack = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_BACK);
    buttonBack.whenPressed(new ElevatorResetEncoderCommand());

    Button buttonLB = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_LB);
    buttonLB.whenPressed(new HatchLoadingStationGroup());

    Button buttonRB = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_RB);
    buttonRB.whenPressed(new CargoShootCommand());

    Button buttonJoyLeft = new JoystickButton(xboxController1, XboxControllerMap.XBOX_JOY_LEFT_BUTTON);
    buttonJoyLeft.whenPressed(new HatchRocketPlaceGroup());

    // MANUAL CO-PILOT CONTROLS

    Button manualButtonLB = new JoystickButton(xboxController2, XboxControllerMap.XBOX_BUTTON_LB);
    manualButtonLB.whenPressed(new HatchPanelArmToggle());

    Button manualButtonRB = new JoystickButton(xboxController2, XboxControllerMap.XBOX_BUTTON_RB);
    manualButtonRB.whenPressed(new HatchPanelGrabToggle());

    Button manualButtonStart = new JoystickButton(xboxController2, XboxControllerMap.XBOX_BUTTON_START);
    manualButtonStart.whenPressed(new ElevatorManualControllerDriveCommand());
  }
}
