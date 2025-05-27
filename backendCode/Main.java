package backendCode;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            OpenAICall agent = new OpenAICall();
            List<int[]> wateringTimes = agent.getWateringTimes("tomato");

            // Create DeviceSchedulers from returned time ranges
            DeviceScheduler water = new DeviceScheduler("WaterPump",
                    wateringTimes.get(0)[0], wateringTimes.get(1)[0], wateringTimes.get(2)[0]);

            DeviceScheduler light = new DeviceScheduler("GrowLight", 7, 20, 18); // BCM 18

            water.start();
            light.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                water.shutdown();
                light.shutdown();
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}