package com.pcpl.carbon.kitkat_service.EventScores.Controller;

import com.pcpl.carbon.kitkat_service.EventScores.Repository.EventScoresRepository;
import com.pcpl.carbon.kitkat_service.EventScores.Response.EventScoresResponse;
import com.pcpl.carbon.kitkat_service.EventScores.Service.EventScoresService;
import com.pcpl.carbon.kitkat_service.EventScores.Service.EventScoresServiceImpl;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.EventScores.DTO.EventScoresDTO;
import com.pcpl.carbon.pcplsdk.EventScores.Model.EventScores;
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
@RequestMapping(value = "/carbon-events-scores")
public class EventScoresController extends AbstractCRUDController<EventScores, EventScoresDTO, EventScoresRepository, EventScoresServiceImpl> {
    private static final Logger logger = LoggerFactory.getLogger(EventScoresController.class);

    @Autowired
    EventScoresService eventScoresService;


    @PostConstruct
    public void setUpServices() {

    }

    @Override
    public ResponseEntity<ApplicationResponse> save(@RequestBody EventScoresDTO dto) {
        if (eventScoresService.checkDuplicate(dto)) {
            return ResponseEntity.status(HttpStatus.OK).body(ApplicationResponse.builder().success(false).error("Duplicate unit type").build());
        } else {
            return super.save(dto);
        }
    }

    @RequestMapping(value = "/get-deleted", method = RequestMethod.POST)
    public EventScoresResponse getDeleted() {
        logger.trace("Entering");
        EventScoresResponse eventScoresResponse = new EventScoresResponse();
        try {
            eventScoresResponse = eventScoresService
                    .getDeleted();
        } catch (Exception ex) {
            eventScoresResponse.setSuccess(false);
            eventScoresResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventScoresResponse;
    }

    @RequestMapping(value = "/get-scores-by-event-id-and-participant-id", method = RequestMethod.POST)
    public EventScoresResponse getScoresByEventIdAndParticipantId(@RequestBody Map<String, String> formData) {
        logger.trace("Entering");
        EventScoresResponse eventScoresResponse = new EventScoresResponse();
        try {
            eventScoresResponse = eventScoresService
                    .getAllByEventIdAndParticipantId(formData);
        } catch (Exception ex) {
            eventScoresResponse.setSuccess(false);
            eventScoresResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventScoresResponse;
    }

    @RequestMapping(value = "/get-scores-by-event-id", method = RequestMethod.POST)
    public EventScoresResponse getScoresByEventId(@RequestBody Map<String, String> formData) {
        logger.trace("Entering");
        EventScoresResponse eventScoresResponse = new EventScoresResponse();
        try {
            eventScoresResponse = eventScoresService
                    .getAllByEventId(formData);
        } catch (Exception ex) {
            eventScoresResponse.setSuccess(false);
            eventScoresResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventScoresResponse;
    }

    @RequestMapping(value = "/export-score-to-excel", method = RequestMethod.POST)
    public EventScoresResponse exportScoreToExcel(@RequestBody Map<String, String> formData) {
        logger.trace("Entering");
        EventScoresResponse eventScoresResponse = new EventScoresResponse();
        try {
            eventScoresResponse = eventScoresService
                    .exportScoreToExcel(formData);
        } catch (Exception ex) {
            eventScoresResponse.setSuccess(false);
            eventScoresResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventScoresResponse;
    }
}
