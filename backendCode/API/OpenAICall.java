package backendCode.API;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.logging.Logger;

// Same imports...

public class OpenAICall {
    private static final Logger logger = Logger.getLogger(OpenAICall.class.getName());
    private final String apiKey;

    public OpenAICall() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("OPENAI_API_KEY");
        logger.info("GOT API KEY: ", apiKey);
        if (this.apiKey == null) {
            logger.severe("API key not found in .env file");
            throw new IllegalStateException("API key not found in .env file");
        }
    }

    public List<int[]> getWateringTimes(String plantName) throws IOException {
        String prompt = "What calendar days of the month should I water a " + plantName + " plant? Respond only in JSON with an array named watering_days that contains integers from 1 to 31. Example: { \"watering_days\": [3, 10, 17, 24] }";
        logger.info("Getting watering times for " + plantName);
        return getDaysFromGPT(prompt, "watering_days");
    }

    public List<int[]> getLightTimes(String plantName) throws IOException {
        String prompt = "What calendar days of the month should I provide light to a " + plantName + " plant? Respond only in JSON with an array named light_days that contains integers from 1 to 31. Example: { \"light_days\": [1, 8, 15, 22] }";
        logger.info("Getting light times for " + plantName);
        return getDaysFromGPT(prompt, "light_days");
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

    private List<int[]> getDaysFromGPT(String prompt, String key) throws IOException {
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
        JSONArray days = jsonContent.getJSONArray(key);

        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < days.length(); i++) {
            result.add(new int[]{days.getInt(i)});
        }

        return result;
    }
}