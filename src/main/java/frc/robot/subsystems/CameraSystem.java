/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
/**
 * Add your docs here.
 */
public class CameraSystem extends Subsystem {
  private static final int CAMERA_FPS = 30;
  private static final int CAMERA_WIDTH = 640;
  private static final int CAMERA_HEIGHT = 360;
  private boolean flipCargo, flipHatch;
  private volatile CvSource outputStream;
  private volatile Mat image = new Mat();
  private volatile CameraType cameraType = CameraType.HATCH;
  private volatile CameraType enabledCameraType = null;
  private Thread visionThread;
  private NetworkTableEntry entry;

  public CameraSystem(boolean flipCargo, boolean flipHatch) {
    this.flipCargo = flipCargo;
    this.flipHatch = flipHatch;
  
    // Output Stream
    outputStream = CameraServer.getInstance().putVideo("Output", 640, 360);

    // Add to ShuffleBoard
    Shuffleboard.getTab("General")
    .add(outputStream)
    .withWidget(BuiltInWidgets.kCameraStream)
    .withProperties(
      Map.of("Show crosshair", true, "Show controls", true)
    )
    // POSITION & SIZE
    .withPosition(2, 0)
    .withSize(4, 4);
  
    entry = Shuffleboard.getTab("General")
    .add("Camera", cameraType.name())
    .withWidget(BuiltInWidgets.kTextView)
    // POSITION & SIZE
    .withPosition(6, 1)
    .withSize(1, 1)
    .getEntry();
  
    // Start thread to process camera feeds
    startVisionThread();
  }

  public void startVisionThread() {
    if (visionThread == null || visionThread.isInterrupted()) {
      visionThread = new Thread(new Runnable() {
        private boolean cargoCamConfigured = false;
        private boolean hatchCamConfigured = false;

        @Override
        public void run() {
          UsbCamera cargoCamera = new UsbCamera("Cargo", 0);
          UsbCamera hatchCamera = new UsbCamera("Hatch", 1);
      
          CvSink cargoVideoSink = CameraServer.getInstance().getVideo(cargoCamera);
          CvSink hatchVideoSink = CameraServer.getInstance().getVideo(hatchCamera);

          while (!Thread.interrupted()) {
            // Enable/disable proper video sinks (Sinks pull in video [in this case from USB])
            if (enabledCameraType != cameraType) {
              cargoVideoSink.setEnabled(cameraType == CameraType.CARGO);
              hatchVideoSink.setEnabled(cameraType == CameraType.HATCH);            
              entry.setString(cameraType.name());
              enabledCameraType = cameraType;
            }
            CvSink activeSink = cameraType == CameraType.CARGO ? cargoVideoSink : hatchVideoSink;
            // Pull image from sink into field 'image'
            activeSink.grabFrame(image, 0.008);
            // Don't process image if it's empty
            if (image == null || image.empty() || image.width() == 0 || image.height() == 0) {
              continue;
            }
            // Configure cameras if needed
            if ((!cargoCamConfigured && cameraType == CameraType.CARGO) || (!hatchCamConfigured && cameraType == CameraType.HATCH)) {
              try {
                switch (cameraType) {
                  case CARGO:
                    cargoCamConfigured = true;
                    //
                    cargoCamera.setFPS(CAMERA_FPS);
                    cargoCamera.setResolution(CAMERA_WIDTH, CAMERA_HEIGHT);
                    break;
                  case HATCH:
                    hatchCamConfigured = true;
                    //hatchCamera.setFPS(CAMERA_FPS);
                    hatchCamera.setResolution(CAMERA_WIDTH, CAMERA_HEIGHT);
                    break;
                }
                // Skip this frame as the next frame will have configuration changes applied
                continue;
              } catch (Exception e) {
                DriverStation.reportWarning(
                  "Could not configure camera " + cameraType + " but it should still work (but using more bandwidth) => "+ e.getMessage(),
                   false);
              }
            }
            // Check if flipping is required
            if ((flipCargo && cameraType == CameraType.CARGO) || (flipHatch && cameraType == CameraType.HATCH)) {
              // 1 means to flip (mirror) around y-axis
              Core.flip(image, image, 1);
            }
            outputStream.putFrame(image);
          }
        }
      }, "Camera Processing");
      visionThread.start();
    }
  }

  public void switchCamera(CameraType type) {
    this.cameraType = type;
  }

  public CvSource getOutputStream() {
    return outputStream;
  }

  public Mat getImage() {
    return image;
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
