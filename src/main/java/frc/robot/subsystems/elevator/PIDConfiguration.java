package frc.robot.subsystems.elevator;

import java.util.function.BiConsumer;

// TODO : Move to a separate class
public enum PIDConfiguration {
    VELOCITY((elevator, mode) -> elevator.configureVelocityPID()),
    POSITION(Elevator::configurePositionPID);

    private BiConsumer<Elevator, Mode> configurator;
    PIDConfiguration(BiConsumer<Elevator, Mode> configurator) {
      this.configurator = configurator;
    }

    public void configurePID(Elevator elevator, Mode mode) {
      configurator.accept(elevator, mode);
    }
}
