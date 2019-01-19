/*
 * FRC Team 4277 mecanum drive subsystem
 * Version1.0
 */

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Robot;
import frc.robot.commands.*;

@SuppressWarnings("deprecation")
public class MecanumDrive extends Subsystem {

	static WPI_TalonSRX FRONT_LEFT_TALON,BACK_LEFT_TALON,FRONT_RIGHT_TALON,BACK_RIGHT_TALON;
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

	public void mecanumDriveJoystick(Joystick driveStick) {
		drive.mecanumDrive_Cartesian(driveStick.getX(), driveStick.getY(), driveStick.getTwist(), 0);
	}
	
	public void fieldOrientedMecanumDriveJoystick(Joystick driveStick, Double gyro) {
		drive.mecanumDrive_Cartesian(driveStick.getX(), driveStick.getY(), driveStick.getTwist(), gyro);
	}
	
	@Override
	protected void initDefaultCommand() {
        setDefaultCommand(new JoystickDriveCommand());
	}
	
}
