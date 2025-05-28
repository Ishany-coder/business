package backendCode.logic;

import backendCode.api.OpenAICall;

import java.io.IOException;
import java.util.List;

public class PlantAdvisor {
    private final OpenAICall ai;

    public PlantAdvisor(OpenAICall ai) {
        this.ai = ai;
    }

    public List<int[]> getWateringSchedule(String plant) throws IOException {
        return ai.getWateringTimes(plant);
    }

    public List<int[]> getLightSchedule(String plant) throws IOException {
        return ai.getLightTimes(plant);
    }
}