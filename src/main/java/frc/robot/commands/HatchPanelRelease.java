package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class HatchPanelRelease extends Command {

    public HatchPanelRelease() {
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
    protected void end() {
        super.end();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}