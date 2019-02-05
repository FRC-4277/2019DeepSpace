package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class HatchPanelGrabToggle extends Command {

    public HatchPanelGrabToggle() {
        requires(Robot.hatchPanelSystem);
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void execute() {
        super.execute();
        Robot.hatchPanelSystem.toggle();
    }

    @Override
    protected void end() {
        super.end();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

}