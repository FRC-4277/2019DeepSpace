/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.autonomous;

import frc.robot.subsystems.motionprofiles.CurveParameters;

/**
 * Add your docs here.
 */
public interface AutonomousConstantsInterface {

static double RIGHT_ROCKET_Y_DISTANCE_1 = 20.0;
static double RIGHT_ROCKET_DURATION = 10.0;

static double RIGHT_ROCKET_ROTATION_TARGET_1 = 80.0;
static double RIGHT_ROCKET_ROTATION_TARGET_1_DELAY = 0.0;
static double RIGHT_ROCKET_ROTATION_TARGET_1_DURATION = 2.0;

static double RIGHT_ROCKET_ROTATION_TARGET_2 = -50.0;
static double RIGHT_ROCKET_ROTATION_TARGET_2_DELAY = 3.0;
static double RIGHT_ROCKET_ROTATION_TARGET_2_DURATION = 2.0;

static CurveParameters RIGHT_ROCKET_CURVE_1 =
    new CurveParameters(RIGHT_ROCKET_ROTATION_TARGET_1, RIGHT_ROCKET_ROTATION_TARGET_1_DURATION, RIGHT_ROCKET_ROTATION_TARGET_1_DELAY);
static CurveParameters RIGHT_ROCKET_CURVE_2 = 
    new CurveParameters(RIGHT_ROCKET_ROTATION_TARGET_2, RIGHT_ROCKET_ROTATION_TARGET_2_DURATION, RIGHT_ROCKET_ROTATION_TARGET_2_DELAY);

}
