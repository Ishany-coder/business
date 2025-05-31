package backendCode;

import backendCode.api.OpenAICall;
import backendCode.logic.PlantAdvisor;
import backendCode.scheduler.DeviceScheduler;

import java.util.List;
import java.util.logging.Logger;

class TimeRange {
    public final int start;
    public final int end;

    public TimeRange(int start, int end) {
        this.start = start;
        this.end = end;
    }
}

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static int getStart(List<TimeRange> times, int index) {
        return index < times.size() ? times.get(index).start : -1;
    }

    private static void printSchedule(String label, List<TimeRange> times) {
        logger.info(label + " Schedule:");
        for (TimeRange t : times) {
            logger.info("Start: " + t.start + ", End: " + t.end);
        }
    }

    private static void validateTimes(List<TimeRange> times, String type) {
        for (TimeRange t : times) {
            if (t.start < 0 || t.start > 23 || t.end < 0 || t.end > 23) {
                throw new IllegalArgumentException("Invalid " + type + " time range: " + t.start + "-" + t.end);
            }
        }
    }

    public static void main(String[] args) {
        try {
            PlantAdvisor advisor = new PlantAdvisor(new OpenAICall());

            String plant = args.length > 0 ? args[0] : "tomato";
            List<TimeRange> wateringTimes = advisor.getWateringSchedule(plant);
            if (wateringTimes.isEmpty()) {
                throw new IllegalStateException("No watering schedule returned for " + plant);
            }
            List<TimeRange> lightTimes = advisor.getLightSchedule(plant);
            if (lightTimes.isEmpty()) {
                throw new IllegalStateException("No light schedule returned for " + plant);
            }

            validateTimes(wateringTimes, "watering");
            validateTimes(lightTimes, "light");

            printSchedule("Watering", wateringTimes);
            printSchedule("Light", lightTimes);

            DeviceScheduler water = new DeviceScheduler("WaterPump",
                    getStart(wateringTimes, 0),
                    getStart(wateringTimes, 1),
                    getStart(wateringTimes, 2));

            DeviceScheduler light = new DeviceScheduler("GrowLight",
                    getStart(lightTimes, 0),
                    getStart(lightTimes, 1),
                    getStart(lightTimes, 2));

            water.start();
            light.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (water != null) {
                    water.shutdown();
                }
                if (light != null) {
                    light.shutdown();
                }
            }));
        } catch (Exception e) {
            logger.severe("Unhandled exception: " + e.getMessage());
        }
    }
}