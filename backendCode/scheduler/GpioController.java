package backendCode.scheduler;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;

public class GpioController {
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
        System.out.println(name + " turned ON");
    }

    public void turnOff() {
        output.low();
        System.out.println(name + " turned OFF");
    }

    public boolean isOn() {
        return output.state() == DigitalState.HIGH;
    }

    public void shutdown() {
        if (pi4j != null) pi4j.shutdown();
    }

    private int resolvePin(String name) {
        return switch (name) {
            case "WaterPump" -> 17;
            case "GrowLight" -> 18;
            default -> throw new IllegalArgumentException("Unknown device: " + name);
        };
    }
}