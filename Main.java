public class Main {
    public static void main(String[] args) {
        try {
            DeviceScheduler water = new DeviceScheduler("WaterPump", 6, 13, 17); // BCM 17
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