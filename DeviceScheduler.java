import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.*;

public class DeviceScheduler {
    private final String name;
    private final int startHour;
    private final int endHour;
    private final GpioController gpio;
    private final ScheduledExecutorService scheduler;

    public DeviceScheduler(String name, int startHour, int endHour, int pin) throws Exception {
        this.name = name;
        this.startHour = startHour;
        this.endHour = endHour;
        this.gpio = new GpioController(pin);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkAndToggle, 0, 1, TimeUnit.MINUTES);
        System.out.println(name + " started: active from " + startHour + ":00 to " + endHour + ":00");
    }

    private void checkAndToggle() {
        try {
            int hour = LocalTime.now().getHour();
            if (hour >= startHour && hour < endHour) {
                if (!gpio.isOn()) {
                    System.out.println("[" + name + "] Turning ON");
                    gpio.turnOn();
                }
            } else {
                if (gpio.isOn()) {
                    System.out.println("[" + name + "] Turning OFF");
                    gpio.turnOff();
                }
            }
        } catch (Exception e) {
            System.err.println("[" + name + "] Error: " + e.getMessage());
        }
    }

    public void shutdown() {
        try {
            System.out.println("[" + name + "] Shutting down.");
            gpio.turnOff();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scheduler.shutdown();
    }
}