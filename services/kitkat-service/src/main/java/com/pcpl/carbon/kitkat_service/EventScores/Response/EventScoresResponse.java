package com.pcpl.carbon.kitkat_service.EventScores.Response;

import com.pcpl.carbon.pcplsdk.EventScores.DTO.EventScoresDTO;
import lombok.Data;

import java.util.List;
@Data
public class EventScoresResponse {
    private EventScoresDTO eventScoresDTO;
    private List<EventScoresDTO> data;
    private long totalPages;
    private long recordsTotal;
    private long currentRecords;
    private long recordsFiltered;
    private boolean success;
    private String error;
    private String excelUploadPath;
}
