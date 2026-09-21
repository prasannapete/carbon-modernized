package com.pcpl.carbon.kitkat_service.EventParticipants.Controller;

import com.pcpl.carbon.kitkat_service.EventParticipants.Repository.EventParticipantsRepository;
import com.pcpl.carbon.kitkat_service.EventParticipants.Response.EventParticipantsResponse;
import com.pcpl.carbon.kitkat_service.EventParticipants.Service.EventParticipantsService;
import com.pcpl.carbon.kitkat_service.EventParticipants.Service.EventsParticipantsServiceImpl;
import com.pcpl.carbon.kitkat_service.KitkatEvents.KkApplaunched.Repository.KkAppLaunchedRepository;
import com.pcpl.carbon.kitkat_service.KitkatEvents.KkApplaunched.Service.KkAppLaunchedServiceImpl;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.EventParticipants.DTO.EventParticipantsDTO;
import com.pcpl.carbon.pcplsdk.EventParticipants.Model.EventParticipants;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.Kitkat.KkAppLaunched.DTO.KkAppLaunchedDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkAppLaunched.Model.KkAppLaunched;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/carbon-event-participants")
public class EventParticipantsController extends AbstractCRUDController<EventParticipants, EventParticipantsDTO, EventParticipantsRepository, EventsParticipantsServiceImpl>{
    private static final Logger logger = LoggerFactory.getLogger(EventParticipantsController.class);

    @Autowired
    EventParticipantsService eventParticipantsService;

    @PostConstruct
    public void setUpServices() {

    }

    @Override
    public ResponseEntity<ApplicationResponse> save(@RequestBody EventParticipantsDTO dto) {
        if (eventParticipantsService.checkDuplicate(dto)) {
            return ResponseEntity.status(HttpStatus.OK).body(ApplicationResponse.builder().success(false).error("Duplicate unit type").build());
        } else {
            return super.save(dto);
        }
    }

    @RequestMapping(value = "/get-deleted", method = RequestMethod.POST)
    public EventParticipantsResponse getDeleted() {
        logger.trace("Entering");
        EventParticipantsResponse eventParticipantsResponse = new EventParticipantsResponse();
        try {
            eventParticipantsResponse = eventParticipantsService
                    .getDeleted();
        } catch (Exception ex) {
            eventParticipantsResponse.setSuccess(false);
            eventParticipantsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventParticipantsResponse;
    }

    @RequestMapping(value = "/get-participants-by-event-id", method = RequestMethod.POST)
    public EventParticipantsResponse getParticipantsByEventId(@RequestBody Map<String, String> formData) {
        logger.trace("Entering");
        EventParticipantsResponse eventParticipantsResponse = new EventParticipantsResponse();
        try {
            eventParticipantsResponse = eventParticipantsService
                    .getAllByEventId(formData);
        } catch (Exception ex) {
            eventParticipantsResponse.setSuccess(false);
            eventParticipantsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventParticipantsResponse;
    }

}
