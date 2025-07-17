public class Main {
    public static void main(String[] args) {
        ApiClient client = new ApiClient("http://raspberrypi.local:4567");

        try {
            // Get system status
            String status = client.getStatus();
            System.out.println("Status: " + status);

            // Start watering
            String waterResult = client.startWatering();
            System.out.println("Watering: " + waterResult);

            // Turn grow light on
            String lightResult = client.setGrowLight("on");
            System.out.println("Grow Light: " + lightResult);

            // Update watering schedule
            String scheduleResult = client.updateWateringSchedule("basil", "08:00");
            System.out.println("Schedule: " + scheduleResult);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
