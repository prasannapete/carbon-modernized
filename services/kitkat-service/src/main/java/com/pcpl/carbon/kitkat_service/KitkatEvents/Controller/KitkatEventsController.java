package com.pcpl.carbon.kitkat_service.KitkatEvents.Controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcpl.carbon.kitkat_service.KitkatEvents.Service.KitkatEventsService;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Kitkat.DTO.EventsDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/kitkat-events")
@Slf4j
@Component
public class KitkatEventsController {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    KitkatEventsService eventsService;


    @RequestMapping(value = "/save-events-redis", method = RequestMethod.POST)
    public ApplicationResponse saveDatatoRedis(@RequestBody EventsDataDTO eventsDataDTO) {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        applicationResponse = eventsService.saveObject("kitkat_events",eventsDataDTO);
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

    @RequestMapping(value = "/get-events-redis", method = RequestMethod.POST)
    public ApplicationResponse getEventDataFromRedis(@RequestBody String key) {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        applicationResponse = eventsService.getObject("kitkat_events");
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

    @RequestMapping(value = "/save-events", method = RequestMethod.POST)
    public ApplicationResponse saveData(@RequestBody EventsDataDTO eventsDataDTO) {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            applicationResponse = eventsService.saveEvents(eventsDataDTO);
        } catch (Exception e) {
            log.error("Error while saving kitkat event", e);
            applicationResponse.setSuccess(false);
            applicationResponse.setMessage("Failed to save kitkat event: " + e.getMessage());
        }
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

}
