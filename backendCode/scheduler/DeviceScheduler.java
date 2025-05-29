package backendCode.scheduler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.*;

public class DeviceScheduler {
    private final String name;
    private final List<Integer> targetDays;
    private final GpioController gpio;
    private final ScheduledExecutorService scheduler;

    public DeviceScheduler(String name, List<Integer> targetDays, int pin) throws Exception {
        this.name = name;
        this.targetDays = targetDays;
        this.gpio = new GpioController(pin);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkAndToggle, 0, 1, TimeUnit.DAYS);
        System.out.printf("[%s] Scheduler started for days: %s%n", name, targetDays);
    }

    private void checkAndToggle() {
        try {
            int today = LocalDate.now().getDayOfMonth();
            boolean shouldBeOn = targetDays.contains(today);

            if (shouldBeOn && !gpio.isOn()) {
                System.out.printf("[%s] Activating on day %d%n", name, today);
                gpio.turnOn();
            } else if (!shouldBeOn && gpio.isOn()) {
                System.out.printf("[%s] Deactivating (not scheduled today)%n", name);
                gpio.turnOff();
            }

            int daysUntilNext = targetDays.stream()
                    .mapToInt(day -> (day - today + 31) % 31)
                    .filter(diff -> diff > 0)
                    .min()
                    .orElse(-1);

            if (daysUntilNext >= 0) {
                System.out.printf("[%s] Next activation in %d day(s)%n", name, daysUntilNext);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
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
