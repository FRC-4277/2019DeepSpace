package frc.robot.commands.HatchGroup;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class HatchPanelExtendArm extends Command {

    public HatchPanelExtendArm() {
        requires(Robot.hatchPanelSystem);
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void execute() {
        super.execute();
        Robot.hatchPanelSystem.extendArm();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
