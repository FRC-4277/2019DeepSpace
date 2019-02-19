/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.hatchcommandgroup;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.StartCommand;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.commands.JoystickDriveCommand;
import frc.robot.commands.hatchgroup.HatchPanelExtendArm;
import frc.robot.commands.hatchgroup.HatchPanelGrabHatch;
import frc.robot.commands.hatchgroup.HatchPanelReleaseHatch;
import frc.robot.commands.hatchgroup.HatchPanelRetractArm;

public class HatchLoadingStationGroup extends CommandGroup {
    /**
     * Add your docs here.
     */
    public HatchLoadingStationGroup() {
        // Add Commands here:
        // e.g
        // TODO : Shorter delays
        addSequential(new HatchPanelReleaseHatch());
        addSequential(new WaitCommand(.05));
        addSequential(new HatchPanelExtendArm());
        addSequential(new WaitCommand(.5));
        addSequential(new HatchPanelGrabHatch());
        addSequential(new WaitCommand(.5));
        addSequential(new HatchPanelRetractArm());
        addSequential(new StartCommand(new JoystickDriveCommand()));
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g
//     addParallel(new Command1());
//     addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
    }
}
