
package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.OI;

public class JoystickDriveCommand extends Command {

	public JoystickDriveCommand() {
		super("JoystickDriveCommand");
		requires(Robot.mecanumDrive);
	}

	@Override
	protected void initialize() {
		super.initialize();
	}

	@Override
	protected void execute() {
		super.execute();
		if (Robot.getInstance().isClone()) {
			// Clone robot has no NavX, so only robot oriented driving
			Robot.mecanumDrive.mecanumDriveJoystick(OI.driveStick);
		} else {
			// Use both field oriented and robot oriented for COMPETITION ROBOT
			if (OI.driveStick.getRawAxis(3) > 0)
				Robot.mecanumDrive.mecanumDriveJoystick(OI.driveStick);

			else if (OI.driveStick.getRawAxis(3) < 0)
				Robot.mecanumDrive.fieldOrientedMecanumDriveJoystick(OI.driveStick, Robot.navX.getAngle());
		}
	}

	@Override
	protected boolean isFinished() {
		// return true when this command no longer needs to run
		return false;
	}

	@Override
	protected void end() {
		super.end();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		super.interrupted();
	}

}