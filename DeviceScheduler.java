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
        System.out.println("%s started: active from %02d:00 to %02d:00n" + name + startHour + endHour);
    }

    private void checkAndToggle() {
        try {
            int hour = LocalTime.now().getHour();
            boolean shouldBeOn = hour >= startHour && hour < endHour;
            if (shouldBeOn && !gpio.isOn()) {
                System.out.printf("[%s] Turning ON%n", name);
                gpio.turnOn();
                }
            else if (!shouldBeOn&& gpio.isOn()) {
                System.out.printf("[%s] Turning OFF%n", name);
                gpio.turnOff();
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void shutdown() {
        try {
            System.out.println("[" + name + "] Shutting down.");
            gpio.turnOff();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        scheduler.shutdown();
    }
}

