package com.pcpl.carbon.kitkat_service.Events.Response;

import com.pcpl.carbon.pcplsdk.Events.DTO.EventsDTO;
import lombok.Data;

import java.util.List;

@Data
public class EventsResponse  {
    private EventsDTO eventsDTO;
    private List<EventsDTO> data;
    private long totalPages;
    private long recordsTotal;
    private long currentRecords;
    private long recordsFiltered;
    private boolean success;
    private String error;

}
