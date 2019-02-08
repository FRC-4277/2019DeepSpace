package frc.robot.commands.hatchgroup;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class HatchPanelGrabHatch extends Command {

    public HatchPanelGrabHatch() {
        requires(Robot.hatchPanelSystem);
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void execute() {
        super.execute();
        Robot.hatchPanelSystem.grabHatch();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
