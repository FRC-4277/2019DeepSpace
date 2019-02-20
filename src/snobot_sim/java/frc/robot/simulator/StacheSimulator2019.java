/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.simulator;

import com.snobot.simulator.ASimulator;
import com.snobot.simulator.SensorActuatorRegistry;
import com.snobot.simulator.wrapper_accessors.DataAccessorFactory;
import com.snobot.simulator.wrapper_accessors.java.JavaI2CWrapperAccessor;
import edu.wpi.first.wpilibj.I2C;
import frc.robot.Robot;

/**
 * Add your docs here.
 */
public class StacheSimulator2019 extends ASimulator {
    public StacheSimulator2019() {
        JavaI2CWrapperAccessor i2cAccessor = (JavaI2CWrapperAccessor) DataAccessorFactory.getInstance().getI2CAccessor();
        i2cAccessor.setI2CFactory(new StacheI2CFactory());
    }

    @Override
    public void update() {
        update(I2C.Port.kMXP);
        update(I2C.Port.kOnboard);
    }

    private void update(I2C.Port port) {
        TMD3782Sim wrapper = (TMD3782Sim) SensorActuatorRegistry.get().getI2CWrappers().get(port.value);
        wrapper.update();
    }
}
