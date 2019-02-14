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
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

/**
 * Add your docs here.
 */
public class CameraSystem extends Subsystem {
  private boolean flipCargo, flipHatch;
  private volatile CvSource outputStream;
  private volatile Mat image = new Mat();
  private volatile CameraType cameraType = CameraType.HATCH;
  private Thread visionThread;

  public CameraSystem(boolean flipCargo, boolean flipHatch) {
    this.flipCargo = flipCargo;
    this.flipHatch = flipHatch;
  
    // Output Stream
    outputStream = CameraServer.getInstance().putVideo("Output", 640, 360);

    // Start thread to process camera feeds
    startVisionThread();

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
  }

  public void startVisionThread() {
    if (visionThread == null || visionThread.isInterrupted()) {
      visionThread = new Thread(new Runnable(){
        @Override
        public void run() {
          UsbCamera cargoCamera = CameraServer.getInstance().startAutomaticCapture("Cargo", 0);
          UsbCamera hatchCamera = CameraServer.getInstance().startAutomaticCapture("Hatch", 1);
      
          cargoCamera.setResolution(640, 360);
          hatchCamera.setResolution(640, 360);
      
          CvSink cargoVideoSink = CameraServer.getInstance().getVideo(cargoCamera);
          CvSink hatchVideoSink = CameraServer.getInstance().getVideo(hatchCamera);

          while (!Thread.interrupted()) {
            // Enable/disable proper video sinks (Sinks pull in video [in this case from USB])
            cargoVideoSink.setEnabled(cameraType == CameraType.CARGO);
            hatchVideoSink.setEnabled(cameraType == CameraType.HATCH);
            CvSink activeSink = cameraType == CameraType.CARGO ? cargoVideoSink : hatchVideoSink;
            // Pull image from sink into field 'image'
            activeSink.grabFrame(image);
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
