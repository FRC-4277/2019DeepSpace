package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class CargoShootCommand extends Command {

    public CargoShootCommand() {
        requires(Robot.cargoSystem);
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void execute() {
        super.execute();
        Robot.cargoSystem.shootBall();
    }

    @Override
    @SuppressWarnings("resource")
    protected void end() {
        new JoystickDriveCommand().start();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

}