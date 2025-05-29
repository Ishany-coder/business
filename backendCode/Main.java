package backendCode;

import backendCode.api.OpenAICall;
import backendCode.logic.PlantAdvisor;
import backendCode.scheduler.DeviceScheduler;

import java.util.List;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static int getStart(List<int[]> times, int index) {
        return index < times.size() ? times.get(index)[0] : -1;
    }

    private static void printSchedule(String label, List<int[]> times) {
        System.out.println(label + " Schedule:");
        for (int[] t : times) {
            System.out.println("Start: " + t[0] + ", End: " + t[1]);
        }
    }

    private static void validateTimes(List<int[]> times, String type) {
        for (int[] t : times) {
            if (t[0] < 0 || t[0] > 23 || t[1] < 0 || t[1] > 23) {
                throw new IllegalArgumentException("Invalid " + type + " time range: " + t[0] + "-" + t[1]);
            }
        }
    }

    public static void main(String[] args) {
        try {
            PlantAdvisor advisor = new PlantAdvisor(new OpenAICall());

            String plant = args.length > 0 ? args[0] : "tomato";
            List<int[]> wateringTimes = advisor.getWateringSchedule(plant);
            if (wateringTimes.isEmpty()) {
                throw new IllegalStateException("No watering schedule returned for " + plant);
            }
            List<int[]> lightTimes = advisor.getLightSchedule(plant);
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