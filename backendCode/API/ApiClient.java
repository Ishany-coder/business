// ApiClient.java
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    private final String baseUrl;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // 1. Get system status
    public String getStatus() throws IOException {
        URL url = new URL(baseUrl + "/status");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return readResponse(conn);
    }

    // 2. Manually start watering
    public String startWatering() throws IOException {
        URL url = new URL(baseUrl + "/water");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        return readResponse(conn);
    }

    // 3. Manually control grow light
    public String setGrowLight(String state) throws IOException {
        URL url = new URL(baseUrl + "/light");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        String json = "{\"state\":\"" + state + "\"}";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        return readResponse(conn);
    }

    // 4. Update watering schedule
    public String updateWateringSchedule(String plant, String time) throws IOException {
        URL url = new URL(baseUrl + "/schedule/watering");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        String json = String.format("{\"plant\":\"%s\",\"time\":\"%s\"}", plant, time);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        return readResponse(conn);
    }

    // Helper to read the response
    private String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}
