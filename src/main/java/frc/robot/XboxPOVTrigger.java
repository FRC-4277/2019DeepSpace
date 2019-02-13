/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.Trigger;

/**
 * Add your docs here.
 */
public class XboxPOVTrigger extends Trigger {
    private XboxController xboxController;
    private Direction direction;

    public XboxPOVTrigger(XboxController xboxController, Direction direction) {
        this.xboxController = xboxController;
        this.direction = direction;
    }

    @Override
    public boolean get() {
        return direction.angleMatches(xboxController.getPOV());
    }

    public enum Direction {
        UP(0),
        TOP_RIGHT(45),
        RIGHT(90),
        BOTTOM_RIGHT(135),
        DOWN(180),
        BOTTOM_LEFT(225),
        LEFT(270),
        TOP_LEFT(315);
        
        private int angle;
        Direction(int angle) {
            this.angle = angle;
        }

        public int getAngle() {
            return angle;
        }

        public boolean angleMatches(int angle) {
            return this.angle == angle;
        }
    }
}
