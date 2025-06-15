package backendCode.logic;

import backendCode.api.OpenAICall;
import backendCode.TimeRange;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class PlantAdvisor {
    private static final Logger logger = Logger.getLogger(DeviceScheduler.class.getName());
    private final OpenAICall ai;

    public PlantAdvisor(OpenAICall ai) {
        this.ai = ai;
    }

    public List<TimeRange> getWateringSchedule(String plant) throws IOException {
        logger.info("Getting watering schedule for " + plant);
        return ai.getWateringTimes(plant);
    }

    public List<TimeRange> getLightSchedule(String plant) throws IOException {
        logger.info("Getting light schedule for " + plant);
        return ai.getLightTimes(plant);
    }
}