package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.commands.HatchGroup.HatchPanelGrabHatch;
import frc.robot.commands.HatchGroup.HatchPanelReleaseHatch;

public class HatchPanelGrabToggle extends Command {

    private boolean hatch = false;

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void execute() {
        super.execute();
        if (hatch) {
            new HatchPanelGrabHatch();
            hatch = false;
        } else {
            new HatchPanelReleaseHatch();
            hatch = true;
        }
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