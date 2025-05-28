package backendCode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import io.github.cdimascio.dotenv.Dotenv;

// Same imports...

public class OpenAICall {

    private final String apiKey;

    public OpenAICall() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("OPENAI_API_KEY");
        if (this.apiKey == null) {
            throw new IllegalStateException("API key not found in .env file");
        }
    }

    public List<int[]> getWateringTimes(String plantName) throws IOException {
        String prompt = "When should I water a " + plantName + " plant? Respond only in JSON with up to 3 objects named watering_times, each having start and end fields using 24-hour integers.";

        return getTimeSlotsFromGPT(prompt, "watering_times");
    }

    public List<int[]> getLightTimes(String plantName) throws IOException {
        String prompt = "When should I provide light to a " + plantName + " plant? Respond only in JSON with up to 3 objects named light_times, each having start and end fields using 24-hour integers.";

        return getTimeSlotsFromGPT(prompt, "light_times");
    }

    private List<int[]> getTimeSlotsFromGPT(String prompt, String key) throws IOException {
        JSONObject requestJson = new JSONObject();
        requestJson.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.put(userMessage);
        requestJson.put("messages", messages);

        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestJson.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
        }

        JSONObject responseJson = new JSONObject(response.toString());
        String content = responseJson.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        JSONObject jsonContent = new JSONObject(content);
        JSONArray times = jsonContent.getJSONArray(key);

        List<int[]> timeSlots = new ArrayList<>();
        for (int i = 0; i < times.length(); i++) {
            JSONObject slot = times.getJSONObject(i);
            int start = slot.getInt("start");
            int end = slot.getInt("end");
            timeSlots.add(new int[]{start, end});
        }

        return timeSlots;
    }
}