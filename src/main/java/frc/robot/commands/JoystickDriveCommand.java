
package frc.robot.commands;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
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
		OI.xboxController1.setRumble(RumbleType.kRightRumble, 0.0);
		JoystickDriveStopOnLineCommand.entry.setBoolean(false);
	}

	@Override
	protected void execute() {
		super.execute();
		// Use both field oriented and robot oriented
		double slider = OI.driveStick.getRawAxis(3);
		if (slider >= 0) 
			Robot.mecanumDrive.mecanumDriveJoystick(OI.driveStick, true);
		else if (slider < 0)
			Robot.mecanumDrive.fieldOrientedMecanumDriveJoystick(OI.driveStick, Robot.navX.getAngle(), true);
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