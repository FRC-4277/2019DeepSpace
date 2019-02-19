/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.ElevatorManualControllerDriveCommand;
import frc.robot.commands.ElevatorMoveToHighCommand;
import frc.robot.commands.ElevatorMoveToHomeCommand;
import frc.robot.commands.ElevatorMoveToLoadingStationCommand;
import frc.robot.commands.ElevatorMoveToMediumCommand;
import frc.robot.commands.ElevatorResetEncoderCommand;
import frc.robot.commands.JoystickDriveCommand;
import frc.robot.XboxPOVTrigger.Direction;
import frc.robot.commands.CameraToggleCommand;
import frc.robot.commands.CargoShootCommand;
import frc.robot.commands.JoystickDriveStopOnLineCommand;
import frc.robot.commands.hatchcommandgroup.HatchLoadingStationGroup;
import frc.robot.commands.hatchcommandgroup.HatchRocketPlaceGroup;
import frc.robot.commands.hatchgroup.HatchPanelExtendArm;
import frc.robot.commands.hatchgroup.HatchPanelGrabHatch;
import frc.robot.commands.hatchgroup.HatchPanelReleaseHatch;
import frc.robot.commands.hatchgroup.HatchPanelRetractArm;
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
  public static XboxController xboxController1 = new XboxController(1);
  // Manual co-pilot controller
  //public static Joystick xboxController2 = new Joystick(2);

  static {

    /*// DRIVESTICK CONTROLS
    Button button5 = new JoystickButton(driveStick, 5);
    button5.whenPressed(new JoystickDriveStopOnLineCommand());
    Button button3 = new JoystickButton(driveStick, 3);
    button3.whenPressed(new JoystickDriveCommand());

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
    */
    
    // DRIVE STICK
    Button driveButton5 = new JoystickButton(driveStick, 5);
    driveButton5.whenPressed(new JoystickDriveStopOnLineCommand());

    Button driveButton3 = new JoystickButton(driveStick, 3);
    driveButton3.whenPressed(new JoystickDriveCommand());

    // CO-PILOT
    Button buttonLB = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_LB);
    buttonLB.whenPressed(new HatchLoadingStationGroup());

    Button buttonJoyRight = new JoystickButton(xboxController1, XboxControllerMap.XBOX_JOY_RIGHT_BUTTON);
    buttonJoyRight.whenPressed(new ElevatorManualControllerDriveCommand());

    Button buttonStart = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_START);
    buttonStart.whenPressed(new ElevatorResetEncoderCommand());

    Button buttonA = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_A);
    buttonA.whenPressed(new ElevatorMoveToHomeCommand(true));

    Button buttonB = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_B);
    buttonB.whenPressed(new ElevatorMoveToLoadingStationCommand(true));

    Button buttonX = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_X);
    buttonX.whenPressed(new ElevatorMoveToMediumCommand(true));

    Button buttonY = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_Y);
    buttonY.whenPressed(new ElevatorMoveToHighCommand(true));

    Button buttonRB = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_RB);
    buttonRB.whenPressed(new HatchRocketPlaceGroup());

    XboxTrigger leftTrigger = new XboxTrigger(xboxController1, Hand.kLeft);
    leftTrigger.whenActive(new CameraToggleCommand());

    XboxTrigger rightTrigger = new XboxTrigger(xboxController1, Hand.kRight);
    rightTrigger.whenActive(new CargoShootCommand());

    Button buttonBack = new JoystickButton(xboxController1, XboxControllerMap.XBOX_BUTTON_BACK);
    buttonBack.whenPressed(new JoystickDriveStopOnLineCommand());

    XboxPOVTrigger upPOV = new XboxPOVTrigger(xboxController1, Direction.UP);
    upPOV.whenActive(new HatchPanelExtendArm());

    XboxPOVTrigger downPOV = new XboxPOVTrigger(xboxController1, Direction.DOWN);
    downPOV.whenActive(new HatchPanelRetractArm());

    XboxPOVTrigger leftPOV = new XboxPOVTrigger(xboxController1, Direction.LEFT);
    leftPOV.whenActive(new HatchPanelGrabHatch());

    XboxPOVTrigger rightPOV = new XboxPOVTrigger(xboxController1, Direction.RIGHT);
    rightPOV.whenActive(new HatchPanelReleaseHatch());
  
    Button buttonLeftJoy = new JoystickButton(xboxController1, XboxControllerMap.XBOX_JOY_LEFT_BUTTON);
    buttonLeftJoy.whenPressed(new JoystickDriveCommand());
  }


}
