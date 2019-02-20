package frc.robot.simulator;

import com.snobot.simulator.SensorActuatorRegistry;
import com.snobot.simulator.module_wrapper.factories.DefaultI2CSimulatorFactory;
import com.snobot.simulator.module_wrapper.interfaces.II2CWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StacheI2CFactory extends DefaultI2CSimulatorFactory {
    private static final String TMD3782_TYPE = "TMD3782";

    @Override
    public boolean create(int aPort, String aType) {
        if (TMD3782_TYPE.endsWith(aType)) {
            TMD3782Sim simulator = new TMD3782Sim(aPort);
            SensorActuatorRegistry.get().register(simulator, aPort);
            return true;
        }

        return super.create(aPort, aType);
    }

    @Override
    protected String getNameForType(II2CWrapper aType) {
        if (aType instanceof TMD3782Sim) {
            return TMD3782_TYPE;
        }

        return super.getNameForType(aType);
    }

    @Override
    public Collection<String> getAvailableTypes() {
        List<String> list = new ArrayList<>(super.getAvailableTypes());
        list.add(TMD3782_TYPE);
        return list;
    }


}
