package backendCode.logic;

import backendCode.api.OpenAICall;
import backendCode.TimeRange;

import java.io.IOException;
import java.util.List;

public class PlantAdvisor {
    private final OpenAICall ai;

    public PlantAdvisor(OpenAICall ai) {
        this.ai = ai;
    }

    public List<TimeRange> getWateringSchedule(String plant) throws IOException {
        return ai.getWateringTimes(plant);
    }

    public List<TimeRange> getLightSchedule(String plant) throws IOException {
        return ai.getLightTimes(plant);
    }
}