/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.autonomous.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.autonomous.*;
import frc.robot.commands.hatchcommandgroup.HatchRocketPlaceGroup;

public class LeftCargoshipHatchCommandGroup extends CommandGroup implements AutonomousConstantsInterface {
  /**
   * Add your docs here.
   */
  public LeftCargoshipHatchCommandGroup() {
    addSequential(new DriveToCommand(0.0, CARGOSHIP_DISTANCE, 0.0, CARGOSHIP_DURATION, true));
    addSequential(new DriveStopOnLineCommand(LINE_UP_STRAFE_SPEED, "right"), 5.0);
    addSequential(new DriveDistanceCorrectionCommand(DISTANCE_CORRECTION_SPEED));
    addSequential(new HatchRocketPlaceGroup(true));
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
  }
}
