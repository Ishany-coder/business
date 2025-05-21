import com.pi4j.Pi4J;
import com.pi4j.context.Context;

public class main {
    public static void main(String[] args) {
        Context pi4j = Pi4J.newAutoContext();

        DeviceController water = new DeviceController("WaterPump", 6, 13, 18, pi4j); // BCM 18
        DeviceController light = new DeviceController("GrowLight", 7, 20, 17, pi4j); // BCM 17

        water.start();
        light.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            water.shutdown();
            light.shutdown();
            pi4j.shutdown();
        }));
    }
}