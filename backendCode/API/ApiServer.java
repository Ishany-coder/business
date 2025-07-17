import static spark.Spark.*;

public class ApiServer {
    public static void start(GpioController gpioController, DeviceScheduler scheduler) {
        port(4567);

        get("/status", (req, res) -> {
            // Return current device states or schedule info as JSON
            return "{ \"status\": \"ok\" }";
        });

        post("/water", (req, res) -> {
            // Trigger watering manually
            gpioController.turnOnWaterPump();
            return "{ \"result\": \"watering started\" }";
        });
        post("/light", (req, res) -> {
            // Trigger light control manually
            gpioController.turnOnLight();
            return "{ "result": "light control started" }";
        });
    }
}