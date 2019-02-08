package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.commands.HatchGroup.HatchPanelExtendArm;
import frc.robot.commands.HatchGroup.HatchPanelRetractArm;

public class HatchPanelArmToggle extends Command {

    private boolean hatch = false;

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void execute() {
        super.execute();
        if (hatch) {
            new HatchPanelExtendArm().start();
            hatch = false;
        } else {
            new HatchPanelRetractArm().start();
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