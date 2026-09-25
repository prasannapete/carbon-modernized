package com.pcpl.carbon.kitkat_service.Events.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.pcpl.carbon.kitkat_service.EventScores.Response.EventScoresResponse;
import com.pcpl.carbon.kitkat_service.EventScores.Service.EventScoresService;
import com.pcpl.carbon.kitkat_service.Events.Repository.EventsRepository;
import com.pcpl.carbon.kitkat_service.Events.Response.EventsResponse;
import com.pcpl.carbon.kitkat_service.User.Service.UserInformationService;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Events.DTO.EventsDTO;
import com.pcpl.carbon.pcplsdk.Events.Model.Events;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class EventsServiceImpl extends AbstractLazyService<Events, EventsDTO, EventsRepository> implements EventsService{

    @Autowired
    EventsRepository eventsRepository;

    @Autowired
    EventScoresService eventScoresService;

    @Autowired
    UserInformationService userInformationService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(EventsServiceImpl.class);

    @Override
    public Events getEntityObject() {
        return new Events();
    }

    @Override
    public EventsDTO getDtoObject() {
        return new EventsDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }

    @Override
    public boolean checkDuplicate(EventsDTO dto) {
        return false;
    }

    @Override
    public EventsResponse getDeleted() throws Exception {
        logger.trace("Entering");
        EventsResponse eventsResponse = new EventsResponse();
        try {
            eventsResponse.setData(
                    getEventDTOS(eventsRepository.findAllByIsDeleted(1)
                    ));
            eventsResponse.setSuccess(true);
            eventsResponse.setError("");
            logger.trace("Completed Successfully");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    @Override
    public EventsResponse getAllEvents() throws Exception {
        logger.trace("Entering");
        EventsResponse eventsResponse = new EventsResponse();
        try {
            eventsResponse.setData(
                    eventsRepository.findAllByIsDeletedEvents()
            );
            eventsResponse.setSuccess(true);
            eventsResponse.setError("");
            logger.trace("Completed Successfully");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    // All non-deleted events (management list) regardless of isLive / date window.
    // Uses the same DTO-constructor mapping as getDeleted()/getAllEvents() so it is
    // independent of the generic /all (ModelMapper) path. Newest first.
    @Override
    public EventsResponse getAllEventsList() throws Exception {
        logger.trace("Entering");
        EventsResponse eventsResponse = new EventsResponse();
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "creationTime");
            List<EventsDTO> eventsDTOS = getEventDTOS(eventsRepository.findAllByIsDeleted(0, sort));
            eventsResponse.setData(eventsDTOS);
            eventsResponse.setRecordsTotal(eventsDTOS.size());
            eventsResponse.setRecordsFiltered(eventsDTOS.size());
            eventsResponse.setSuccess(true);
            eventsResponse.setError("");
            logger.trace("Completed Successfully");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    @Override
    public EventsResponse getLeaderboardEvents(Map<String, String> formData) throws Exception {
        logger.trace("Entering");
        EventsResponse eventsResponse = new EventsResponse();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDate = (localDateTime).format(formatter);
            Date currentDate = simpleDateFormat.parse(formattedDate);
            Sort sort = Sort.by(Sort.Direction.ASC, "creationTime");
            List<EventsDTO> eventsDTOS = (eventsRepository.findAllByIsDeletedAndIsLiveAndValidDate(currentDate,sort));
            for(EventsDTO eventsDTO: eventsDTOS){
                Map<String, String> formData1 = new HashMap<>();
                formData1.put("current_page",formData.get("current_page"));
                formData1.put("page_size",formData.get("page_size"));
                formData1.put("eventId",String.valueOf(eventsDTO.getId()));
                if(eventsDTO.getScoreType()==0){
                    formData1.put("sort_field","score");
                    formData1.put("sort_order","desc");
                }else{
                    formData1.put("sort_field","time");
                    formData1.put("sort_order","asc");
                }
                EventScoresResponse eventScoresResponse = eventScoresService.getAllByEventId(formData1);
                eventsDTO.setEventScoresDTOList(eventScoresResponse.getData());

            }
            eventsResponse.setData(eventsDTOS);
            eventsResponse.setSuccess(true);
            eventsResponse.setError("");
            logger.trace("Completed Successfully");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    @Override
    public EventsResponse moveToTrash(Map<String, String> formData) throws Exception {
        User userInfo = userInformationService.getUser();
        logger.trace("Entering");
        EventsResponse eventsResponse = new EventsResponse();
        try {
            logger.trace("Data:{}", objectMapper.writeValueAsString(formData));
            Optional<Events> optionalEvents = eventsRepository.findById(Long.valueOf(formData.get("id")));
            if (optionalEvents.isPresent()) {
                Events events = optionalEvents.get();
                events.setIsDeleted(1);
                events.setDeletedBy(userInfo.getId());
                events.setDeletedTime(new Date());
                eventsResponse.setSuccess(true);
                eventsResponse.setError("");
                eventsResponse.setEventsDTO(
                        new EventsDTO(
                                eventsRepository.save(events)
                        ));
            } else {
                eventsResponse.setSuccess(false);
                eventsResponse.setError("Error occurred while moving events to trash!! Please try after sometime");
            }
            logger.trace("Completed Successfully");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventsResponse.setSuccess(false);
            eventsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventsResponse;
    }

    private List<EventsDTO> getEventDTOS(List<Events> eventsList) {
        List<EventsDTO> eventsDTOS = new ArrayList<>();
        for (Events events : eventsList) {
            eventsDTOS.add(new EventsDTO(events));
        }
        return eventsDTOS;
    }
}