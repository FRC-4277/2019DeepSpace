/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.autonomous.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.StartCommand;
import frc.robot.commands.ElevatorMoveToHighCommand;
import frc.robot.commands.ElevatorMoveToHomeCommand;
import frc.robot.commands.autonomous.DriveStopOnLineCommand;
import frc.robot.commands.autonomous.DriveToCommand;
import frc.robot.commands.hatchcommandgroup.HatchRocketPlaceGroup;

public class LeftRocketLeftHighHatchGroup extends CommandGroup {
  /**
   * Add your docs here.
   */
  public LeftRocketLeftHighHatchGroup() {
    // Add Commands here:
    // e.g. addSequential(new Command1());
    // addSequential(new Command2());
    // these will run in order.

    // Drive to left edge of rocket, lined up with side
    addSequential(new DriveToCommand(0.0, 12.0, -30.0, 4.7, false));
    // Strafe left to line up with line
    addSequential(new DriveStopOnLineCommand(0.4, "left"));
    // Move elevator to high (false makes it so next command runs when elevator reaches height)
    // Also don't require elevator subsystem, so later calls to elevator won't interrupt command group
    addSequential(new ElevatorMoveToHighCommand(false, false));
    // Now, keep running PID loop to keep elevator at high
    addSequential(new StartCommand(new ElevatorMoveToHighCommand(true)));
    // Place hatch
    addSequential(new HatchRocketPlaceGroup());
    // After rocket place group is done, move elevator to home
    addSequential(new StartCommand(new ElevatorMoveToHomeCommand(true)));

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
  }
}
