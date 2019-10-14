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

static double RIGHT_ROCKET_Y_DISTANCE_1 = 15;
static double RIGHT_ROCKET_DURATION = 5.0;

static double RIGHT_ROCKET_ROTATION_TARGET_1 = -60.0;
static double RIGHT_ROCKET_ROTATION_TARGET_1_DELAY = 1.0;
static double RIGHT_ROCKET_ROTATION_TARGET_1_DURATION = 2.0;

static double RIGHT_ROCKET_ROTATION_TARGET_2 = 40.0;
static double RIGHT_ROCKET_ROTATION_TARGET_2_DELAY = 3.0;
static double RIGHT_ROCKET_ROTATION_TARGET_2_DURATION = 2.0;

//static double RIGHT_ROCKET_TOWARD_WALL_ROTATION_TARGET = 180 + RIGHT_ROCKET_ROTATION_TARGET_2;
//static double RIGHT_ROCKET_TOWARD_WALL_DURATION = 2;

//static double RETURN_TO_WALL_DISTANCE = 14.4;
//static double RETURN_TO_WALL_DURATION = 5.0;

static CurveParameters ZERO_CURVE = new CurveParameters(0, 0, 0);
static CurveParameters RIGHT_ROCKET_CURVE_1 =
    new CurveParameters(RIGHT_ROCKET_ROTATION_TARGET_1, RIGHT_ROCKET_ROTATION_TARGET_1_DURATION, RIGHT_ROCKET_ROTATION_TARGET_1_DELAY);
static CurveParameters RIGHT_ROCKET_CURVE_2 = 
    new CurveParameters(RIGHT_ROCKET_ROTATION_TARGET_2, RIGHT_ROCKET_ROTATION_TARGET_2_DURATION, RIGHT_ROCKET_ROTATION_TARGET_2_DELAY);

static double LEFT_ROCKET_Y_DISTANCE_1 = 15;
static double LEFT_ROCKET_DURATION = 5.0;
    
static double LEFT_ROCKET_ROTATION_TARGET_1 = 60.0;
static double LEFT_ROCKET_ROTATION_TARGET_1_DELAY = 1.0;
static double LEFT_ROCKET_ROTATION_TARGET_1_DURATION = 2.0;
    
static double LEFT_ROCKET_ROTATION_TARGET_2 = -40.0;
static double LEFT_ROCKET_ROTATION_TARGET_2_DELAY = 3.0;
static double LEFT_ROCKET_ROTATION_TARGET_2_DURATION = 2.0;

static CurveParameters LEFT_ROCKET_CURVE_1 =
    new CurveParameters(LEFT_ROCKET_ROTATION_TARGET_1, LEFT_ROCKET_ROTATION_TARGET_1_DURATION, LEFT_ROCKET_ROTATION_TARGET_1_DELAY);
static CurveParameters LEFT_ROCKET_CURVE_2 = 
    new CurveParameters(LEFT_ROCKET_ROTATION_TARGET_2, LEFT_ROCKET_ROTATION_TARGET_2_DURATION, LEFT_ROCKET_ROTATION_TARGET_2_DELAY);

static double CARGOSHIP_DISTANCE = 12.7
;
static double CARGOSHIP_DURATION = 5.2;

static double LINE_UP_STRAFE_SPEED = 0.3;
static double DISTANCE_CORRECTION_SPEED = 0.25;

}
