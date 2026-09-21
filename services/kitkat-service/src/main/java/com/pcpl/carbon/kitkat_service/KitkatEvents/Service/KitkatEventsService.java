package com.pcpl.carbon.kitkat_service.KitkatEvents.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Kitkat.DTO.EventsDataDTO;

public interface KitkatEventsService {
    public ApplicationResponse saveObject(String key, EventsDataDTO eventsDataDTO);
   public ApplicationResponse saveEvents(EventsDataDTO eventsDataDTO);
    public ApplicationResponse getObject(String key);
}
