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
	private static final double DRIVE_JOYSTICK_DEADBAND = 0.10;

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
			FRONT_RIGHT_TALON.setInverted(true);
			BACK_RIGHT_TALON.setInverted(true);
		}
	}

	public void mecanumDriveJoystick(Joystick driveStick, boolean applyDeadband) {
		double x = driveStick.getX();
		double y = driveStick.getY();
		double twist = driveStick.getTwist();
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
		double twist = driveStick.getTwist();
		if (applyDeadband) {
			x = applyDeadband(x);
			y = applyDeadband(y);
			twist = applyDeadband(twist);
		}
		drive.mecanumDrive_Cartesian(x, y, twist, gyro);
	}

	/**
	 * Applies a transfer function that has a deadband as defined by {@link MecanumDrive#DRIVE_JOYSTICK_DEADBAND}.
	 * Output will be scaled to between -1.0 to 1.0 to allow full range of motor output regardless of deadband range.
	 * @param axisValue Axis value from Joystick [-1.0..1.0]
	 * @return output [-1.0..1.0]
	 */
	private double applyDeadband(double axisValue) {
		if (Math.abs(axisValue) < DRIVE_JOYSTICK_DEADBAND) {
			// Just return 0.0 as axis value is in the dead zone
			return 0.0;
		} else {
			/* 
			 * Scale value to allow for same range of control (-1.0 to 1.0) as if deadband wasn't there
			 * (This is useful so the motor outputs less than the deadband are still usable)
			 * Joystick Axis Value -> Motor Power graph: https://www.desmos.com/calculator/nxf9qxgc5d
			 */
			return (axisValue - (Math.abs(axisValue) / axisValue * DRIVE_JOYSTICK_DEADBAND)) / (1 - DRIVE_JOYSTICK_DEADBAND);
		}
	}

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new JoystickDriveCommand());
	}

}
