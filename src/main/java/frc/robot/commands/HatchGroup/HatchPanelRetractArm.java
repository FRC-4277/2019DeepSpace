package frc.robot.commands.HatchGroup;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class HatchPanelRetractArm extends Command {

    public HatchPanelRetractArm() {
        requires(Robot.hatchPanelSystem);
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void execute() {
        super.execute();
        Robot.hatchPanelSystem.retractArm();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
