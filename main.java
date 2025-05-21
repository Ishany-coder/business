import com.pi4j.io.gpio.RaspiPin;

public class main {
    public static void main(String[] args) {
        // Water pump: on from 6 AM to 1 PM
        DeviceController waterController = new DeviceController("WaterPump", 6, 13, RaspiPin.GPIO_01);

        // Grow light: on from 7 AM to 8 PM
        DeviceController lightController = new DeviceController("GrowLight", 7, 20, RaspiPin.GPIO_02);

        waterController.start();
        lightController.start();

        // Safe shutdown on Ctrl+C or reboot
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            waterController.shutdown();
            lightController.shutdown();
        }));
    }
}