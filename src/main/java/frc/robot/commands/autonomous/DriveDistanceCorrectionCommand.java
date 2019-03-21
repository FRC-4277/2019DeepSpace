package frc.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class DriveDistanceCorrectionCommand extends Command {

  private boolean isAtDistance;
  private double speed;

  public DriveDistanceCorrectionCommand(Double speed){
    requires(Robot.mecanumDrive);
    this.speed = speed;
  }

  @Override
  protected void initialize(){

  }

  @Override
  protected void execute() {
    isAtDistance = !Robot.proximitySensorHatch.get();

    if (isAtDistance) {
      return;
    }

    Robot.mecanumDrive.mecanumDrive(0, speed, 0, Robot.lineUpGyro.getAngle(), false);
  }
  @Override
  protected boolean isFinished(){
    return isAtDistance;
  }

  @Override
  protected void end(){
    Robot.mecanumDrive.mecanumStop();
  }

  @Override
  protected void interrupted(){
    Robot.mecanumDrive.mecanumStop();
  }

}
