package com.pcpl.carbon.kitkat_service.Events.Controller;

import com.pcpl.carbon.kitkat_service.Events.Repository.EventsRepository;
import com.pcpl.carbon.kitkat_service.Events.Response.EventsResponse;
import com.pcpl.carbon.kitkat_service.Events.Service.EventsService;
import com.pcpl.carbon.kitkat_service.Events.Service.EventsServiceImpl;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Events.DTO.EventsDTO;
import com.pcpl.carbon.pcplsdk.Events.Model.Events;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/carbon-events")
public class EventsController extends AbstractCRUDController<Events, EventsDTO, EventsRepository, EventsServiceImpl> {
    private static final Logger logger = LoggerFactory.getLogger(EventsController.class);

    @Autowired
    EventsService eventsService;

    @PostConstruct
    public void setUpServices() {

    }

    @Override
    public ResponseEntity<ApplicationResponse> save(@RequestBody EventsDTO dto) {
        if (eventsService.checkDuplicate(dto)) {
            return ResponseEntity.status(HttpStatus.OK).body(ApplicationResponse.builder().success(false).error("Duplicate unit type").build());
        } else {
            return super.save(dto);
        }
    }


    @RequestMapping(value = "/get-deleted", method = RequestMethod.POST)
    public EventsResponse getDeleted() {
        logger.trace("Entering");
        EventsResponse eventsResponse = new EventsResponse();
        try {
            eventsResponse = eventsService.getDeleted();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    @RequestMapping(value = "/get-all-events", method = RequestMethod.POST)
    public EventsResponse getAllEvents() {
        logger.trace("Entering");
        EventsResponse eventsResponse = new EventsResponse();
        try {
            eventsResponse = eventsService.getAllEvents();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    @RequestMapping(value = "/get-leader-board-events", method = RequestMethod.POST)
    public EventsResponse getLeaderBoardEvents(@RequestBody Map<String, String> formData) {
        logger.trace("Entering");
        EventsResponse eventsResponse =  new EventsResponse();
        try {
            eventsResponse = eventsService.getLeaderboardEvents(formData);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    @RequestMapping(value = "/trash", method = RequestMethod.POST)
    public EventsResponse moveToTrash(@RequestBody Map<String, String> formData) {
        logger.trace("Entering");
        EventsResponse eventsResponse = new EventsResponse();
        try {
            eventsResponse = eventsService.moveToTrash(formData);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    @RequestMapping(value = "/keep-session", method = RequestMethod.POST)
    public boolean keepSession() {
        return true;
    }
}
