package frc.robot.commands.hatchgroup;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class HatchPanelReleaseHatch extends Command {

    public HatchPanelReleaseHatch() {
        requires(Robot.hatchPanelSystem);
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void execute() {
        super.execute();
        Robot.hatchPanelSystem.releaseHatch();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
