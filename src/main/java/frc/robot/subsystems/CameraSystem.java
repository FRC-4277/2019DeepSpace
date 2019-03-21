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
import frc.robot.Robot;
import frc.robot.utils.Settings;
import frc.robot.utils.Settings.ChooserSetting;
import frc.robot.utils.Settings.Setting;

/**
 * Subsystem to manage switching cameras and streaming them
 */
public class CameraSystem extends Subsystem {
  private final Setting<Integer> cameraFpsSetting =
          Settings.createIntField("Camera FPS", true)
                  .defaultValue(20)
                  .build();
  private final Setting<Integer> cameraWidthSetting =
          Settings.createIntField("Camera Width", true)
                  .defaultValue(320)
                  .build();
  private final Setting<Integer> cameraHeightSetting =
          Settings.createIntField("Camera Height", true)
                  .defaultValue(240)
                  .build();
  private final ChooserSetting<CameraType> cameraTypeSetting =
          Settings.createEnumChooser(CameraType.class, "Camera")
                  .addAll(CameraType.HATCH)
                  .build();
  private UsbCamera cargoCamera, hatchCamera;
  private VideoSink server;
  private CameraType cameraType = CameraType.HATCH;
  private CameraType enabledCameraType = null;
  private NetworkTableEntry cameraTypeDisplay;

  public CameraSystem() {
    cargoCamera = CameraServer.getInstance().startAutomaticCapture(0);
    if (Robot.isReal()) {
      cargoCamera.setFPS(cameraFpsSetting.getValue());
      cargoCamera.setResolution(cameraWidthSetting.getValue(), cameraHeightSetting.getValue());
    }

    hatchCamera = CameraServer.getInstance().startAutomaticCapture(1);
    if (Robot.isReal()) {
      hatchCamera.setFPS(cameraFpsSetting.getValue());
      hatchCamera.setResolution(cameraWidthSetting.getValue(), cameraHeightSetting.getValue());
    }

    // Setting Update Listeners
    cameraFpsSetting.addUpdateListener(fps -> {
      cargoCamera.setFPS(fps);
      hatchCamera.setFPS(fps);
    });
    cameraWidthSetting.addUpdateListener(width -> {
      int height = cameraHeightSetting.getValue();
      cargoCamera.setResolution(width, height);
      hatchCamera.setResolution(width, height);
    });
    cameraHeightSetting.addUpdateListener(height -> {
      int width = cameraWidthSetting.getValue();
      cargoCamera.setResolution(width, height);
      hatchCamera.setResolution(width, height);
    });
    cameraTypeSetting.addUpdateListener(type -> {
      if (type != null) {
        switchCamera(type);
      }
    });

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
            .withPosition(0, 0)
            .withSize(4, 4);

    /* Add Camera Name to Shuffleboard */
    cameraTypeDisplay = Shuffleboard.getTab("General")
            .add("Camera", cameraType.name())
            .withWidget(BuiltInWidgets.kTextView)
            // POSITION & SIZE
            .withPosition(7, 1)
            .withSize(1, 1)
            .getEntry();

    // Default to HATCH camera
    switchCamera(CameraType.HATCH);
  }

  public void switchCamera(CameraType type) {
    this.cameraType = type;
    if (enabledCameraType != type) {
      server.setSource(type == CameraType.CARGO ? cargoCamera : hatchCamera);
      cameraTypeSetting.setValue(type);
      cameraTypeDisplay.setString(type.name());
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
