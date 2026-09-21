package com.pcpl.carbon.kitkat_service.EventParticipants.Service;

import com.pcpl.carbon.kitkat_service.EventParticipants.Response.EventParticipantsResponse;
import com.pcpl.carbon.pcplsdk.EventParticipants.DTO.EventParticipantsDTO;
import com.pcpl.carbon.pcplsdk.EventParticipants.Model.EventParticipants;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface EventParticipantsService {

    boolean checkDuplicate(EventParticipantsDTO eventParticipantsDTO);

    EventParticipantsResponse getDeleted() throws Exception;

    EventParticipantsResponse getAllByEventId(Map<String, String> formData) throws Exception;

}
