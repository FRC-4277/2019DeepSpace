/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.autonomous.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.StartCommand;
import frc.robot.commands.*;
import frc.robot.commands.autonomous.*;
import frc.robot.commands.hatchcommandgroup.HatchRocketPlaceGroup;
import frc.robot.subsystems.elevator.Mode;

public class GoToRightRocketRight extends CommandGroup implements AutonomousConstantsInterface  {
  /**
   * Add your docs here.
   */
  public GoToRightRocketRight() {
    // Add Commands here:
    // e.g. addSequential(new Command1());
    // addSequential(new Command2());
    // these will run in order.

    // To run multiple commands at the same time,
    // use addParallel()
    // e.g. addParallel(new Command1());
    // addSequential(new Command2());
    // Command1 and Command2 will run in parallel.

    // A command group will require all of the subsystems that each member
    // would require.
    // e.g. if Command1 requires chassis, and Command2 requires arm,
    // a CommandGroup containing them would require both the chassis and the
    // arm.

    addSequential(new DriveToCommand(0.0, RIGHT_ROCKET_Y_DISTANCE_1, RIGHT_ROCKET_DURATION, false, ZERO_CURVE, RIGHT_ROCKET_CURVE_1, RIGHT_ROCKET_CURVE_2));
    addSequential(new DriveStopOnLineCommand(LINE_UP_STRAFE_SPEED, "right"), 2.0);
    addSequential(new DriveDistanceCorrectionCommand(0.25));
    addSequential(new ElevatorMoveToHighCommand(), 2.0);
    addSequential(new HatchRocketPlaceGroup(false));
    addParallel(new ElevatorStayAtCommand(Mode.HIGH),1.0);
    addSequential(new ElevatorMoveToHomeCommand(),1.5);
    //addSequential(new DriveToCommand(0.0,0.0,RIGHT_ROCKET_TOWARD_WALL_ROTATION_TARGET,RIGHT_ROCKET_TOWARD_WALL_DURATION,false));
    //addSequential(new DriveToCommand(0.0,-RETURN_TO_WALL_DISTANCE,0,RETURN_TO_WALL_DURATION,true));
    addSequential(new StartCommand(new JoystickDriveCommand()));
  }
}
