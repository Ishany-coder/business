package backendCode;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            OpenAICall agent = new OpenAICall();

            // Get dynamic watering and lighting times for the plant
            String plantName = "tomato";
            List<int[]> wateringTimes = agent.getWateringTimes(plantName);
            List<int[]> lightTimes = agent.getLightTimes(plantName);

            // Use first 3 time slots for scheduling devices
            DeviceScheduler water = new DeviceScheduler("WaterPump",
                    wateringTimes.get(0)[0],
                    wateringTimes.size() > 1 ? wateringTimes.get(1)[0] : -1,
                    wateringTimes.size() > 2 ? wateringTimes.get(2)[0] : -1);

            DeviceScheduler light = new DeviceScheduler("GrowLight",
                    lightTimes.get(0)[0],
                    lightTimes.size() > 1 ? lightTimes.get(1)[0] : -1,
                    lightTimes.size() > 2 ? lightTimes.get(2)[0] : -1);

            // Start both schedulers
            water.start();
            light.start();

            // Gracefully shut down on exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                water.shutdown();
                light.shutdown();
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}