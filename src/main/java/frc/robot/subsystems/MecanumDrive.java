/*
 * FRC Team 4277 mecanum drive subsystem
 * Version1.0
 */

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Robot;
import frc.robot.commands.*;

@SuppressWarnings("deprecation")
public class MecanumDrive extends Subsystem {
	private static final double DRIVE_JOYSTICK_DEADBAND = 0.5;

	static WPI_TalonSRX FRONT_LEFT_TALON, BACK_LEFT_TALON, FRONT_RIGHT_TALON, BACK_RIGHT_TALON;
	RobotDrive drive;

	public MecanumDrive(int frontLeftMotor, int backLeftMotor, int frontRightMotor, int backRightMotor) {
		FRONT_LEFT_TALON = new WPI_TalonSRX(frontLeftMotor);
		BACK_LEFT_TALON = new WPI_TalonSRX(backLeftMotor);
		FRONT_RIGHT_TALON = new WPI_TalonSRX(frontRightMotor);
		BACK_RIGHT_TALON = new WPI_TalonSRX(backRightMotor);

		drive = new RobotDrive(FRONT_LEFT_TALON, BACK_LEFT_TALON, FRONT_RIGHT_TALON, BACK_RIGHT_TALON);

		FRONT_LEFT_TALON.setExpiration(1);
		BACK_LEFT_TALON.setExpiration(1);
		FRONT_RIGHT_TALON.setExpiration(1);
		BACK_RIGHT_TALON.setExpiration(1);

		if (Robot.getInstance().isClone()) {
			FRONT_LEFT_TALON.setInverted(true);
			BACK_LEFT_TALON.setInverted(true);
		} else {
			FRONT_LEFT_TALON.setInverted(true);
			BACK_LEFT_TALON.setInverted(true);
		}
	}

	public void mecanumDriveJoystick(Joystick driveStick, boolean applyDeadband) {
		double x = driveStick.getX();
		double y = driveStick.getY();
		double twist = -driveStick.getTwist();
		if (applyDeadband) {
			x = applyDeadband(x);
			y = applyDeadband(y);
			twist = applyDeadband(twist);
		}
		drive.mecanumDrive_Cartesian(x, y, twist, 0);
	}

	public void fieldOrientedMecanumDriveJoystick(Joystick driveStick, double gyro, boolean applyDeadband) {
		double x = driveStick.getX();
		double y = driveStick.getY();
		double twist = -driveStick.getTwist();
		if (applyDeadband) {
			x = applyDeadband(x);
			y = applyDeadband(y);
			twist = applyDeadband(twist);
		}
		drive.mecanumDrive_Cartesian(x, y, twist, gyro);
	}

	/**
	 * Applies a deadband as defined by {@link MecanumDrive#DRIVE_JOYSTICK_DEADBAND}.
	 * @param axisValue Axis value from Joystick [-1.0..1.0]
	 * @return output [-1.0..1.0]
	 */
	private double applyDeadband(double axisValue) {
		if (Math.abs(axisValue) < DRIVE_JOYSTICK_DEADBAND) {
			// Just return 0.0 as axis value is in the dead zone
			return 0.0;
		} else {
			return axisValue;
		}
	}

	public void mecanumDrive(double x, double y, double twist) {
		mecanumDrive(x, y, twist, 0);
	}

	public void mecanumDrive(double x, double y, double twist, double gyro) {
		drive.mecanumDrive_Cartesian(x, y, twist, gyro);
	}

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new JoystickDriveCommand());
	}

}
