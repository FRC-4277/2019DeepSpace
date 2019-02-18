/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.util.Map;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

/**
 * Subsystem to manage switching cameras and streaming them
 */
public class CameraSystem extends Subsystem {
  private static final int CAMERA_FPS = 20;
  private static final int CAMERA_WIDTH = 320;
  private static final int CAMERA_HEIGHT = 240;
  private UsbCamera cargoCamera, hatchCamera;
  private VideoSink server;
  private CameraType cameraType = CameraType.HATCH;
  private CameraType enabledCameraType = null;
  private NetworkTableEntry entry;

  public CameraSystem() {
    cargoCamera = CameraServer.getInstance().startAutomaticCapture(0);
    cargoCamera.setFPS(CAMERA_FPS);
    cargoCamera.setResolution(CAMERA_WIDTH, CAMERA_HEIGHT);
    
    hatchCamera = CameraServer.getInstance().startAutomaticCapture(1);
    hatchCamera.setFPS(CAMERA_FPS);
    hatchCamera.setResolution(CAMERA_WIDTH, CAMERA_HEIGHT);
    
    server = CameraServer.getInstance().addSwitchedCamera("Current");

    VideoSource serverSource = server.getSource();
    // Grab VideoSource named "Current"
    for (VideoSource source : VideoSource.enumerateSources()) {
      if (source.getName().equals("Current")) {
        serverSource = source;
        break;
      }
    }

    /* Add Camera to Shuffleboard */
    Shuffleboard.getTab("General")
    .add(serverSource)
    .withWidget(BuiltInWidgets.kCameraStream)
    .withProperties(
      Map.of("Show crosshair", true, "Show controls", true)
    )
    // POSITION & SIZE
    .withPosition(2, 0)
    .withSize(4, 4);
  
    /* Add Camera Name to Shuffleboard */
    entry = Shuffleboard.getTab("General")
    .add("Camera", cameraType.name())
    .withWidget(BuiltInWidgets.kTextView)
    // POSITION & SIZE
    .withPosition(6, 1)
    .withSize(1, 1)
    .getEntry();

    // Default to HATCH camera
    switchCamera(CameraType.HATCH);
  }

  public void switchCamera(CameraType type) {
    this.cameraType = type;
    if (enabledCameraType != type) {
      server.setSource(type == CameraType.CARGO ? cargoCamera : hatchCamera);
      entry.setString(type.name());
      enabledCameraType = type;
    }
  }

  public CameraType getCurrentType() {
    return this.cameraType;
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  public enum CameraType {
    CARGO, HATCH;
  }
}
