package com.pcpl.carbon.kitkat_service.EventParticipants.Response;

import com.pcpl.carbon.pcplsdk.EventParticipants.DTO.EventParticipantsDTO;
import lombok.Data;

import java.util.List;

@Data
public class EventParticipantsResponse {
    private EventParticipantsDTO eventParticipantsDTO;
    private List<EventParticipantsDTO> data;
    private long totalPages;
    private long recordsTotal;
    private long currentRecords;
    private long recordsFiltered;
    private boolean success;
    private String error;
}
