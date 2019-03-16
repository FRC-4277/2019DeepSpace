package frc.robot.utils;

import edu.wpi.first.wpilibj.RobotController;

public class RobotTime {
  public static double getFPGASeconds() {
    return RobotController.getFPGATime() / 1000000.0;
  }
}
