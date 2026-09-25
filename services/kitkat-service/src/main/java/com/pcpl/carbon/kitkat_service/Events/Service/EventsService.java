package com.pcpl.carbon.kitkat_service.Events.Service;

import com.pcpl.carbon.kitkat_service.Events.Response.EventsResponse;
import com.pcpl.carbon.pcplsdk.Events.DTO.EventsDTO;

import java.util.Map;

public interface EventsService {

    
    

    EventsResponse getDeleted() throws Exception;

    EventsResponse getAllEvents() throws Exception;

    EventsResponse getAllEventsList() throws Exception;

    EventsResponse getLeaderboardEvents(Map<String,String> formData) throws Exception;

    EventsResponse moveToTrash(Map<String, String> formData) throws Exception;

    boolean checkDuplicate(EventsDTO dto);
}
