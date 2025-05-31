package backendCode.scheduler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class DeviceScheduler {
    private static final Logger logger = Logger.getLogger(DeviceScheduler.class.getName());
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
        logger.info(String.format("[%s] Scheduler started for days: %s", name, targetDays));
    }

    private void checkAndToggle() {
        try {
            int today = LocalDate.now().getDayOfMonth();
            boolean shouldBeOn = targetDays.contains(today);

            if (shouldBeOn && !gpio.isOn()) {
                logger.info(String.format("[%s] Activating on day %d", name, today));
                gpio.turnOn();
            } else if (!shouldBeOn && gpio.isOn()) {
                logger.info(String.format("[%s] Deactivating (not scheduled today)", name));
                gpio.turnOff();
            }

            int daysUntilNext = targetDays.stream()
                .mapToInt(day -> (day - today + 31) % 31)
                .filter(diff -> diff > 0)
                .min()
                .orElse(-1);

            if (daysUntilNext >= 0) {
                logger.info(String.format("[%s] Next activation in %d day(s)", name, daysUntilNext));
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to toggle device: " + name, ex);
        }
    }

    public void shutdown() {
        try {
            logger.info("[" + name + "] Shutting down.");
            gpio.turnOff();
        } catch (IOException e) {
            logger.warning("[" + name + "] Error while shutting down: " + e.getMessage());
        }
        scheduler.shutdown();
    }
}
