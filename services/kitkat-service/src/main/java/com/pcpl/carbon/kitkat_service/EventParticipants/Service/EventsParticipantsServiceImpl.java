package com.pcpl.carbon.kitkat_service.EventParticipants.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcpl.carbon.kitkat_service.EventParticipants.Repository.EventParticipantsRepository;
import com.pcpl.carbon.kitkat_service.EventParticipants.Response.EventParticipantsResponse;
import com.pcpl.carbon.pcplsdk.Common.Utility.MapperUtility;
import com.pcpl.carbon.pcplsdk.EventParticipants.DTO.EventParticipantsDTO;
import com.pcpl.carbon.pcplsdk.EventParticipants.Model.EventParticipants;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class EventsParticipantsServiceImpl extends AbstractLazyService<EventParticipants, EventParticipantsDTO, EventParticipantsRepository> implements EventParticipantsService{

    @Autowired
    EventParticipantsRepository eventParticipantsRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(EventsParticipantsServiceImpl.class);

    public EventsParticipantsServiceImpl() {
        ModelMapper mapper = MapperUtility.getInstance();

        mapper.getConfiguration()
                .setAmbiguityIgnored(true);

        mapper.typeMap(EventParticipants.class, EventParticipantsDTO.class)
                .addMappings(mapping -> {
                    mapping.skip(EventParticipantsDTO::setEventTime);
                    mapping.skip(EventParticipantsDTO::setEventScores);
                    mapping.skip(EventParticipantsDTO::setEventScoreId);
                    mapping.skip(EventParticipantsDTO::setEventScoreType);
                    mapping.skip(EventParticipantsDTO::setEventName);
                });
    }
    @Override
    public EventParticipants getEntityObject() {
        return new EventParticipants();
    }

    @Override
    public EventParticipantsDTO getDtoObject() {
        return new EventParticipantsDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getName";
    }

    @Override
    public boolean checkDuplicate(EventParticipantsDTO eventParticipantsDTO) {
        List<EventParticipants> eventParticipants;
        if (eventParticipantsDTO.getId()==null) {
            eventParticipants = eventParticipantsRepository
                    .findAllByIsDeletedAndEventIdAndName(0, eventParticipantsDTO.getEventId(),eventParticipantsDTO.getName());
        } else {
            eventParticipants = eventParticipantsRepository
                    .findAllByIsDeletedAndEventIdAndNameAndIdIsNot(0, eventParticipantsDTO.getEventId(), eventParticipantsDTO.getName(),eventParticipantsDTO.getId());
        }
        if (!eventParticipants.isEmpty()) {
            return true;
        }else {
            return false;
        }
    }


    @Override
    public EventParticipantsResponse getDeleted() throws Exception {
        logger.trace("Entering");
        EventParticipantsResponse eventParticipantsResponse = new EventParticipantsResponse();
        try {
            eventParticipantsResponse.setData(
                    getEventsParticipantsDTOS(eventParticipantsRepository.findAllByIsDeleted(1)
                    ));
            eventParticipantsResponse.setSuccess(true);
            eventParticipantsResponse.setError("");
            logger.trace("Completed Successfully");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventParticipantsResponse.setSuccess(false);
            eventParticipantsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventParticipantsResponse;
    }




    @Override
    public EventParticipantsResponse getAllByEventId(Map<String, String> formData) throws Exception {
        logger.trace("Entering");
        EventParticipantsResponse eventParticipantsResponse = new EventParticipantsResponse();
        try {
            logger.trace("Data:{}", objectMapper.writeValueAsString(formData));
            int pageNumber = formData.get("current_page") == null ? 0 : Integer.parseInt(formData.get("current_page"));
            int pageSize = formData.get("page_size") == null ? 10 : Integer.parseInt(formData.get("page_size"));
            String sortFiled = formData.get("sort_field") == null ? "creationTime" : formData.get("sort_field");
            String sortOrder = formData.get("sort_order") == null ? "asc" : formData.get("sort_order");
            Sort sort;
            if (sortOrder.equals("asc")) {
                sort = Sort.by(Sort.Direction.ASC, sortFiled);
            } else {
                sort = Sort.by(Sort.Direction.DESC, sortFiled);
            }
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            Page<EventParticipantsDTO> page;
            page = eventParticipantsRepository.findAllByEventId(
                    Long.valueOf(formData.get("eventId")),
                    pageable
            );
            eventParticipantsResponse.setRecordsTotal(page.getTotalElements());
            eventParticipantsResponse.setRecordsFiltered(page.getTotalElements());
            eventParticipantsResponse.setTotalPages(page.getTotalPages());
            eventParticipantsResponse.setData(
                    (page.getContent())
            );
            eventParticipantsResponse.setCurrentRecords(eventParticipantsResponse.getData().size());
            eventParticipantsResponse.setSuccess(true);
            logger.trace("Completed Successfully");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            eventParticipantsResponse.setSuccess(false);
            eventParticipantsResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return eventParticipantsResponse;
    }


    private List<EventParticipantsDTO> getEventsParticipantsDTOS(List<EventParticipants> eventParticipants) {
        List<EventParticipantsDTO> eventParticipantsDTOS = new ArrayList<>();
        for (EventParticipants eventParticipants1 : eventParticipants) {
            eventParticipantsDTOS.add(new EventParticipantsDTO(eventParticipants1));
        }
        return eventParticipantsDTOS;
    }

}
