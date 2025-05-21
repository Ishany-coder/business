import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import java.time.LocalTime;
import java.util.concurrent.*;

public class DeviceController {
    private final String name;
    private final int startHour;
    private final int endHour;
    private final DigitalOutput output;
    private final ScheduledExecutorService scheduler;

    public DeviceController(String name, int startHour, int endHour, int bcmPin, Context pi4j) {
        this.name = name;
        this.startHour = startHour;
        this.endHour = endHour;

        this.output = pi4j.create(DigitalOutput.newConfigBuilder(pi4j)
                .id(name)
                .name(name)
                .address(bcmPin)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output"));

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkTimeAndToggle, 0, 1, TimeUnit.MINUTES);
        System.out.println(name + " started: " + startHour + ":00 to " + endHour + ":00 daily");
    }

    private void checkTimeAndToggle() {
        int currentHour = LocalTime.now().getHour();
        if (currentHour >= startHour && currentHour < endHour) {
            if (output.state() == DigitalState.LOW) {
                System.out.println("[" + name + "] ON");
                output.high();
            }
        } else {
            if (output.state() == DigitalState.HIGH) {
                System.out.println("[" + name + "] OFF");
                output.low();
            }
        }
    }

    public void shutdown() {
        System.out.println("Shutting down " + name);
        output.low();
        scheduler.shutdown();
    }
}