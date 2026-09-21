package com.pcpl.carbon.kitkat_service.KitkatEvents.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcpl.carbon.kitkat_service.KitkatEvents.KkApplaunched.Service.KkAppLaunchedServiceImpl;
import com.pcpl.carbon.kitkat_service.KitkatEvents.KkBrandViewedGarage.Service.KkBrandViewedGarageServiceImpl;
import com.pcpl.carbon.kitkat_service.KitkatEvents.KkCarUnlocked.Service.KkCarUnlockedServiceImpl;
import com.pcpl.carbon.kitkat_service.KitkatEvents.KkRacePlayed.Service.KkRacePlayedServiceImpl;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Kitkat.DTO.EventsDataDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkAppLaunched.DTO.KkAppLaunchedDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkBrandViewedGarage.DTO.KkBrandViewedGarageDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkCarUnlocked.DTO.KkCarUnlockedDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkRacePlayed.DTO.KkRacePlayedDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class KitkatEventsServiceImpl implements KitkatEventsService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KkAppLaunchedServiceImpl  kkAppLaunchedServiceImpl;

    @Autowired
    KkBrandViewedGarageServiceImpl kkBrandViewedGarageServiceImpl;

    @Autowired
    KkCarUnlockedServiceImpl kkCarUnlockedServiceImpl;

    @Autowired
    KkRacePlayedServiceImpl kkRacePlayedServiceImpl;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public void RedisObjectService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public ApplicationResponse saveObject(String key, EventsDataDTO eventsDataDTO) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            String jsonString = objectMapper.writeValueAsString(eventsDataDTO);
            ListOperations<String, String> ops = redisTemplate.opsForList();
            ops.rightPush(key,jsonString);
            applicationResponse.setData(eventsDataDTO);
            applicationResponse.setSuccess(true);
            applicationResponse.setMessage("Object saved successfully.");
            applicationResponse.setError(null);
        } catch (JsonProcessingException e) {
            applicationResponse.setError("Failed to save object."+e.getMessage());
            applicationResponse.setSuccess(false);

            throw new RuntimeException("Failed to serialize object", e);
        }
        return applicationResponse;
    }

    @Override
    public ApplicationResponse getObject(String key) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        ListOperations<String, String> ops = redisTemplate.opsForList();
        List<String> jsonStrings = ops.range("kitkat_events", 0, 4);
        try {
            List<EventsDataDTO> eventsList = new ArrayList<>();
            for (String json : jsonStrings) {
                EventsDataDTO event = objectMapper.readValue(json, EventsDataDTO.class);
                eventsList.add(event);
            }
            applicationResponse.setData(eventsList);
            applicationResponse.setSuccess(true);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize object", e);
        }
        return applicationResponse;
    }

    @Override
    public ApplicationResponse saveEvents(EventsDataDTO eventsDataDTO) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            switch (eventsDataDTO.getEventType()){
                case 1:
                    KkAppLaunchedDTO kkAppLaunchedDTO = objectMapper.convertValue(eventsDataDTO.getEvents(), KkAppLaunchedDTO.class);
                    applicationResponse.setMessage("KkAppLaunched event saved successfully!");
                    applicationResponse.setData(kkAppLaunchedServiceImpl.save(kkAppLaunchedDTO));
                    applicationResponse.setSuccess(true);
                    break;
                case 2:
                    KkCarUnlockedDTO kkCarUnlockedDTO = objectMapper.convertValue(eventsDataDTO.getEvents(), KkCarUnlockedDTO.class);
                    applicationResponse.setMessage("KkCar unlocked event saved successfully!");
                    applicationResponse.setData(kkCarUnlockedServiceImpl.save(kkCarUnlockedDTO));
                    applicationResponse.setSuccess(true);
                    break;
                case 3:
                    KkRacePlayedDTO kkRacePlayedDTO = objectMapper.convertValue(eventsDataDTO.getEvents(), KkRacePlayedDTO.class);
                    applicationResponse.setMessage("Kk Race Played event saved successfully!");
                    applicationResponse.setData(kkRacePlayedServiceImpl.save(kkRacePlayedDTO));
                    applicationResponse.setSuccess(true);
                    break;
                case 5:
                    KkBrandViewedGarageDTO kkBrandViewedGarageDTO = objectMapper.convertValue(eventsDataDTO.getEvents(), KkBrandViewedGarageDTO.class);
                    applicationResponse.setMessage("Kk Brand viewed garage event saved successfully!");
                    applicationResponse.setData(kkBrandViewedGarageServiceImpl.save(kkBrandViewedGarageDTO));
                    applicationResponse.setSuccess(true);
                    break;
                default:
                    applicationResponse.setMessage("The type caste is not working");
                    applicationResponse.setSuccess(false);
                    break;
            }
        }catch (Exception e){
            log.error(e.getMessage());
            applicationResponse.setError(e.getMessage());
            applicationResponse.setSuccess(false);
        }
        return applicationResponse;
    }
}