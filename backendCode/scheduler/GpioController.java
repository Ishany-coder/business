package backendCode.scheduler;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import java.util.logging.Logger;

public class GpioController implements AutoCloseable {
    private static final Logger logger = Logger.getLogger(GpioController.class.getName());
    private final String name;
    private final int pin;
    private final Context pi4j;
    private final DigitalOutput output;

    public GpioController(String name) {
        this.name = name;
        this.pin = resolvePin(name);  // Map device to pin
        this.pi4j = Pi4J.newAutoContext();
        this.output = DigitalOutput.newConfigBuilder(pi4j)
                .id(name)
                .name(name)
                .address(pin)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output")
                .build();
        pi4j.create(output);
    }

    public void turnOn() {
        output.high();
        logger.info(name + " turned ON");
    }

    public void turnOff() {
        output.low();
        logger.info(name + " turned OFF");
    }

    public boolean isOn() {
        logger.info(name + "is" + (output.state() == DigitalState.HIGH ? "ON" : "OFF"));
        return output.state() == DigitalState.HIGH;
    }

    public void shutdown() {
        logger.info("Shutting down: ", name);
        if (pi4j != null) pi4j.shutdown();
    }

    private int resolvePin(String name) {  
        logger.info("Resolving pin for device: " + name);
        return switch (name) {
            case "WaterPump" -> 17;
            case "GrowLight" -> 18;
            default -> throw new IllegalArgumentException("Unknown device: " + name);
        };
    }

    @Override
    public void close() {
        logger.info("CLosing: ", name);
        shutdown();
    }
}