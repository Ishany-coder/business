import com.pi4j.io.gpio.*;
import java.time.LocalTime;
import java.util.concurrent.*;

public class DeviceController {
    private final String name;
    private final int startHour;
    private final int endHour;
    private final GpioController gpio;
    private final GpioPinDigitalOutput pin;
    private final ScheduledExecutorService scheduler;

    public DeviceController(String name, int startHour, int endHour, Pin gpioPin) {
        this.name = name;
        this.startHour = startHour;
        this.endHour = endHour;

        this.gpio = GpioFactory.getInstance();
        this.pin = gpio.provisionDigitalOutputPin(gpioPin, name, PinState.LOW);
        this.pin.setShutdownOptions(true, PinState.LOW);

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkAndUpdate, 0, 1, TimeUnit.MINUTES);
        System.out.println(name + " controller started (Active from " + startHour + " to " + endHour + ").");
    }

    private void checkAndUpdate() {
        int currentHour = LocalTime.now().getHour();
        if (currentHour >= startHour && currentHour < endHour) {
            if (pin.isLow()) {
                System.out.println("[" + name + "] ON");
                pin.high();
            }
        } else {
            if (pin.isHigh()) {
                System.out.println("[" + name + "] OFF");
                pin.low();
            }
        }
    }

    public void shutdown() {
        System.out.println("Shutting down " + name + " controller.");
        pin.low();
        gpio.shutdown();
        scheduler.shutdown();
    }
}