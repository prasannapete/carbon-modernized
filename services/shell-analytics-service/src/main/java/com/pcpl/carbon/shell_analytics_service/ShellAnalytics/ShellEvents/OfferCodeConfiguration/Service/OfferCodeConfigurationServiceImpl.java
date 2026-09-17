package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferCodeConfiguration.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CodeConfigurationTranslationDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.OfferCodeConfigurationDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.CodeConfigurationTranslation;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.OfferCodeConfiguration;
import com.pcpl.carbon.shell_analytics_service.Common.Util.EventTypeConstants;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CodeConfigurationTranslation.Repository.CodeConfigurationTranslationRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferCodeConfiguration.Repository.OfferCodeConfigurationRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferIssueDetails.Service.OfferIssueDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OfferCodeConfigurationServiceImpl extends AbstractLazyService<OfferCodeConfiguration, OfferCodeConfigurationDTO, OfferCodeConfigurationRepository> implements OfferCodeConfigurationService {

    @Autowired
    OfferCodeConfigurationRepository offerCodeConfigurationRepository;

    @Autowired
    CodeConfigurationTranslationRepository codeConfigurationTranslationRepository;

    @Override
    public OfferCodeConfiguration getEntityObject() {
        return new OfferCodeConfiguration();
    }

    @Override
    public OfferCodeConfigurationDTO getDtoObject() {
        return new OfferCodeConfigurationDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }

    @Override
    public ApplicationResponse getByEventTypeAndCountryCode(Map<String, String> formData) throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try{
            Optional<OfferCodeConfiguration> offerCodeConfiguration = offerCodeConfigurationRepository.findAllByIsDeletedAndEventTypeAndCountryCode(0, Integer.parseInt(formData.get("eventType")), formData.get("countryCode"));
            if(offerCodeConfiguration.isPresent()){
                applicationResponse.setData(offerCodeConfiguration.get());
                applicationResponse.setSuccess(true);
                applicationResponse.setError("");
            }
            else{
                applicationResponse.setData(null);
                applicationResponse.setSuccess(false);
                applicationResponse.setError("No data present for given event type and country code.");
            }
        }catch (Exception e){
            applicationResponse.setError(e.getMessage());
            applicationResponse.setSuccess(false);
        }
        return applicationResponse;
    }

    @Override
    public ApplicationResponse getByCountryCode(Map<String, String> formData) throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try{
            List<OfferCodeConfigurationDTO> offerCodeConfiguration = offerCodeConfigurationRepository.findAllByIsDeletedAndCountryCode(formData.get("countryCode"));
//            for (OfferCodeConfigurationDTO dto : offerCodeConfiguration) {
//                String eventTypeText = OfferIssueDetailsServiceImpl.EventTypeUtil.getEventText(dto.getEventType());
                List<CodeConfigurationTranslationDTO> codeConfigurationTranslations = codeConfigurationTranslationRepository.findAllByIsDeletedCountryCode(formData.get("countryCode"));
            Map<String, List<CodeConfigurationTranslationDTO>> groupedByLang =
                    codeConfigurationTranslations.stream()
                            .collect(Collectors.groupingBy(CodeConfigurationTranslationDTO::getLangCode));

            Map<String, List<Map<String, Object>>> languageGrouped = codeConfigurationTranslations.stream()
                    .collect(Collectors.groupingBy(
                            CodeConfigurationTranslationDTO::getLangCode,
                            Collectors.mapping(dto -> {
                                Map<String, Object> challengeData = new HashMap<>();
                                challengeData.put("eventTypeText", dto.getEventTitle());
                                challengeData.put("rewardText",dto.getEventReward());
                                challengeData.put("description", dto.getEventDescription());
                                challengeData.put("howToClaim", dto.getEventHowToClaim());
                                challengeData.put("eventType", dto.getEventType());
                                challengeData.put("isActive", dto.getIsActive()==1?true:false);
                                return challengeData;
                            }, Collectors.toList())
                    ));

            List<Map<String, Object>> finalData = languageGrouped.entrySet().stream()
                    .map(entry -> {
                        Map<String, Object> languageBlock = new HashMap<>();
                        languageBlock.put("languageID", entry.getKey());
                        languageBlock.put("challengeData", entry.getValue());
                        return languageBlock;
                    }).collect(Collectors.toList());
        applicationResponse.setData(finalData);
            if(((List<OfferCodeConfigurationDTO>) applicationResponse.getData()).size()==0){
                applicationResponse.setData(offerCodeConfiguration);
                applicationResponse.setSuccess(true);
                applicationResponse.setError("");
            }
            if(((List<OfferCodeConfigurationDTO>) applicationResponse.getData()).size()>0){
                applicationResponse.setSuccess(true);
                applicationResponse.setError("");
            }
            else{
                applicationResponse.setData(null);
                applicationResponse.setSuccess(false);
                applicationResponse.setError("No data present for given country code.");
            }
        }catch (Exception e){
            applicationResponse.setError(e.getMessage());
            applicationResponse.setSuccess(false);
        }
        return applicationResponse;
    }

    public class EventTypeUtil {
        public static String getEventText(int eventType) {
            switch (eventType) {
                case EventTypeConstants.EVENT_LOGIN:
                    return EventTypeConstants.EVENT_LOGIN_TEXT;
                case EventTypeConstants.EVENT_FIRST_CAR:
                    return EventTypeConstants.EVENT_FIRST_CAR_TEXT;
                case EventTypeConstants.EVENT_SECOND_CAR:
                    return EventTypeConstants.EVENT_SECOND_CAR_TEXT;
                case EventTypeConstants.EVENT_THIRD_CAR:
                    return EventTypeConstants.EVENT_THIRD_CAR_TEXT;
                case EventTypeConstants.EVENT_FOURTH_CAR:
                    return EventTypeConstants.EVENT_FOURTH_CAR_TEXT;
                case EventTypeConstants.EVENT_FIFTH_CAR:
                    return EventTypeConstants.EVENT_FIFTH_CAR_TEXT;
                case EventTypeConstants.WINNER_OF_THE_RACE :
                    return EventTypeConstants.WINNER_OF_THE_RACE_TEXT;
                case EventTypeConstants.WINNER_2_10_PLACE :
                    return EventTypeConstants.WINNER_2_10_PLACE_TEXT;
                case EventTypeConstants.WINNER_SECOND_PLACE:
                    return EventTypeConstants.WINNER_SECOND_PLACE_TEXT;
                case EventTypeConstants.WINNER_THIRD_PLACE:
                    return EventTypeConstants.WINNER_THIRD_PLACE_TEXT;
                case EventTypeConstants.WINNER_4_10_PLACE:
                    return EventTypeConstants.WINNER_4_10_PLACE_TEXT;
                default:
                    return "UNKNOWN EVENT";
            }
        }
    }
}
