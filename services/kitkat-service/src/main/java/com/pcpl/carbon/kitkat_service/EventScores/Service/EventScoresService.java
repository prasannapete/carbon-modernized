package com.pcpl.carbon.kitkat_service.EventScores.Service;

import com.pcpl.carbon.kitkat_service.EventScores.Response.EventScoresResponse;
import com.pcpl.carbon.pcplsdk.EventScores.DTO.EventScoresDTO;

import java.util.Map;


public interface EventScoresService {

    EventScoresResponse getDeleted() throws Exception;

    EventScoresResponse getAllByEventIdAndParticipantId(Map<String, String> formData) throws Exception;

    EventScoresResponse getAllByEventId(Map<String, String> formData) throws Exception;

    EventScoresResponse exportScoreToExcel(Map<String, String> formData) throws Exception;

    boolean checkDuplicate(EventScoresDTO dto);
}
