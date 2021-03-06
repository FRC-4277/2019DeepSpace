/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.robot.commands.JoystickDriveCommand;
import frc.robot.commands.autonomous.groups.LeftCargoshipHatchCommandGroup;
import frc.robot.commands.autonomous.groups.RightCargoshipHatchCommandGroup;
import frc.robot.commands.hatchgroup.HatchPanelGrabHatch;
import frc.robot.commands.hatchgroup.HatchPanelRetractArm;
import frc.robot.map.CloneRobotMap;
import frc.robot.map.CompetitionRobotMap;
import frc.robot.map.RobotMap;
import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  public static Robot instance;
  public static OI m_oi;

  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();
  SendableChooser<Boolean> m_cloneChooser = new SendableChooser<>();
  

  public static RobotMap map;
  public static MecanumDrive mecanumDrive;
  public static Elevator elevator;
  public static HatchPanelSystem hatchPanelSystem;
  public static CargoSystem cargoSystem;
  public static CameraSystem cameraSystem;
  public static MotionProfile motionProfile;

  public static AHRS navX;
  public static ColorProximitySensor hatchColorSensor, cargoColorSensor;
  public static ADXRS450_Gyro lineUpGyro;

  public Compressor compressor;

  private NetworkTableEntry gameTimeEntry;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    instance = this;

    // m_chooser.setDefaultOption("Default Auto", new ExampleCommand());
    // chooser.addOption("My Auto", new MyAutoCommand());

    // Robot Chooser
    m_cloneChooser.setDefaultOption("Competition", false);
    m_cloneChooser.addOption("Clone", true);
    SmartDashboard.putData("Robot", m_cloneChooser);

    instansiateSubsystems();

     //Autonomous Choose
     SmartDashboard.putData("Auto mode", m_chooser);
     m_chooser.setDefaultOption("Driver Control", new JoystickDriveCommand()); // Driver Controlled
     m_chooser.addOption("Left Hatch Cargoship", new LeftCargoshipHatchCommandGroup()); // Left Cargo Ship Hatch
     m_chooser.addOption("Right Hatch Cargoship", new RightCargoshipHatchCommandGroup()); // Right Cargo Ship Hatch


    navX = new AHRS(SPI.Port.kMXP);
    navX.reset();

    lineUpGyro = new ADXRS450_Gyro();

    hatchColorSensor = new ColorProximitySensor(I2C.Port.kOnboard);
    cargoColorSensor = new ColorProximitySensor(I2C.Port.kMXP);

    LiveWindow.addSensor("MecanumDrive", "NavX", navX);

    gameTimeEntry = Shuffleboard.getTab("General")
    .add("Game Time", "XXX")
    .withWidget(BuiltInWidgets.kTextView)
    // POSITION & SIZE
    .withPosition(8, 0)
    .withSize(1, 1)
    .getEntry();
  }

  public boolean isClone() {
    return m_cloneChooser.getSelected();
  }

  public RobotMap updateMap() {
    // If clone is selected, then set and return a new CloneRobotMap,
    // otherwise do the same for ComeptitionRobotMap
    System.out.println("Robot changed to " + (isClone() ? "CLONE" : "COMPETITION"));
    return map = isClone() ? new CloneRobotMap() : new CompetitionRobotMap();
  }

  public void instansiateSubsystems() {
    updateMap();
    if (mecanumDrive == null) {
      mecanumDrive = new MecanumDrive(map.getFrontLeftTalon(), map.getBackLeftTalon(), map.getFrontRightTalon(),
          map.getBackRightTalon());
    }
    if (elevator == null) {
      elevator = new Elevator(map.getElevatorTalon(), map.getElevatorFollowerTalon());
    }
    if (hatchPanelSystem == null) {
      hatchPanelSystem = new HatchPanelSystem(map.getPCMId());
    }
    if (cargoSystem == null) {
      cargoSystem = new CargoSystem(map.getPCMId());
    }
    if (compressor == null) {
      compressor = new Compressor(map.getPCMId());
    }
    if (cameraSystem == null) {
      cameraSystem = new CameraSystem(/* Flip Cargo Cam */ false, /* Flip Hatch Cam */ false);
    }
    if (m_oi == null) {
      m_oi = new OI();
    }
    if (motionProfile == null){
      motionProfile = new MotionProfile();
    }
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    gameTimeEntry.setNumber(DriverStation.getInstance().getMatchTime());
  }

  /**
   * This function is called once each time the robot enters Disabled mode. You
   * can use it to reset any subsystem information you want to clear when the
   * robot is disabled.
   */
  @Override
  public void disabledInit() {
    new HatchPanelGrabHatch().start();
    new HatchPanelRetractArm().start();
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString code to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons to
   * the switch structure below with additional strings & commands.
   */
  @Override
  public void autonomousInit() {
    navX.reset();
    instansiateSubsystems();
    m_autonomousCommand = m_chooser.getSelected();

    /*
     * String autoSelected = SmartDashboard.getString("Auto Selector", "Default");
     * switch(autoSelected) { case "My Auto": autonomousCommand = new
     * MyAutoCommand(); break; case "Default Auto": default: autonomousCommand = new
     * ExampleCommand(); break; }
     */

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.start();
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    instansiateSubsystems();
    // Switch tab to General
    Shuffleboard.selectTab("General");
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
    compressor.setClosedLoopControl(true);
    SmartDashboard.putNumber("X", navX.getRawGyroX());
    SmartDashboard.putNumber("Y", navX.getRawGyroY());
    SmartDashboard.putNumber("Z", navX.getRawGyroZ());
    SmartDashboard.putNumber("Angle", navX.getAngle());
  }

  @Override
  public void testInit() {
    instansiateSubsystems();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public static Robot getInstance() {
    return instance;
  }
}
