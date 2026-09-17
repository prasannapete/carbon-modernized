package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferIssueDetails.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.OfferCodeConfigurationDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.OfferIssueDetailsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.LoyaltyOauthCredentials;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.OfferCodeConfiguration;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.OfferIssueDetails;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.UserToken;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Response.*;
import com.pcpl.carbon.shell_analytics_service.Common.Util.EventTypeConstants;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CodeConfigurationTranslation.Repository.CodeConfigurationTranslationRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.LoyaltyOauthCredentials.Repository.LoyaltyOauthCredentialsRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferCodeConfiguration.Repository.OfferCodeConfigurationRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferIssueDetails.Repository.OfferIssueDetailsRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserToken.Repository.UserTokenRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserToken.Service.UserTokenService;
import com.pcpl.carbon.shell_analytics_service.config.SrlAnalyticsConfig;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Slf4j
public class OfferIssueDetailsServiceImpl extends AbstractLazyService<OfferIssueDetails, OfferIssueDetailsDTO, OfferIssueDetailsRepository> implements OfferIssueDetailsService {
    @Override
    public OfferIssueDetails getEntityObject() {
        return new OfferIssueDetails();
    }

    @Override
    public OfferIssueDetailsDTO getDtoObject() {
        return new OfferIssueDetailsDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }

    @Autowired
    OfferIssueDetailsRepository offerIssueDetailsRepository;

    @Autowired
    RestTemplate loadBalanced;

    @Autowired
    SrlAnalyticsConfig srlAnalyticsConfig;

    @Autowired
    OfferCodeConfigurationRepository offerCodeConfigurationRepository;

    @Autowired
    CodeConfigurationTranslationRepository codeConfigurationTranslationRepository;

    @Autowired
    UserTokenService userTokenService;

    @Autowired
    UserTokenRepository userTokenRepository;

    @Autowired
    LoyaltyOauthCredentialsRepository loyaltyOauthCredentialsRepository;

    @Override
    public ApplicationResponse saveOfferIssueDetails(OfferIssueDetails offerIssueDetails) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try{
            OfferIssueDetailsDTO saveOfferIssueDetails = convertEntityToDto(offerIssueDetailsRepository.save(offerIssueDetails));
            String eventText = null;
            if(saveOfferIssueDetails.getCountryCode().equals("BG")){
                 eventText = EventTypeUtil.getBGEventText(saveOfferIssueDetails.getEventType());

            }else {
                 eventText = EventTypeUtil.getEventText(saveOfferIssueDetails.getEventType());
            }
            saveOfferIssueDetails.setEventTypeText(eventText);
            applicationResponse.setData(saveOfferIssueDetails);
            applicationResponse.setSuccess(true);
            applicationResponse.setMessage("Object saved successfully.");
            applicationResponse.setError(null);
        }
        catch (Exception e){
            applicationResponse.setError("Failed to save object."+e.getMessage());
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

        public static String getBGEventText(int eventType) {
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
                case EventTypeConstants.BG_WINNER_OF_THE_RACE :
                    return EventTypeConstants.BG_WINNER_OF_THE_RACE_TEXT;
                case EventTypeConstants.BG_WINNER_SECOND_PLACE:
                    return EventTypeConstants.BG_WINNER_SECOND_PLACE_TEXT;
                case EventTypeConstants.BG_WINNER_THIRD_PLACE:
                    return EventTypeConstants.BG_WINNER_THIRD_PLACE_TEXT;
                case EventTypeConstants.BG_WINNER_4_10_PLACE:
                    return EventTypeConstants.BG_WINNER_4_10_PLACE_TEXT;
                default:
                    return "UNKNOWN EVENT";
            }
        }
    }

    @Override
    public ApplicationResponse sendOffers(Map<String, String> formData) throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        TokenResponse token = getDigestToken();
        String consumerUuid = null;
        HttpHeaders headersForUserAccessToken = new HttpHeaders();
        headersForUserAccessToken.setContentType(MediaType.APPLICATION_JSON);
        headersForUserAccessToken.set("Authorization", "Basic " + token.getToken());

        UserAccessTokenResponse userAccessTokenResponse=new UserAccessTokenResponse();
        UserTokenResponse userTokenResponse=new UserTokenResponse();
        if(formData.get("consumerUuid")==null || formData.get("consumerUuid").isEmpty()) {
            try {
                JSONObject postDataForUserAccessToken = new JSONObject();
                postDataForUserAccessToken.put("accessCode", formData.get("accessCode"));

                HttpEntity<String> entity = new HttpEntity<>(postDataForUserAccessToken.toJSONString(), headersForUserAccessToken);

                ResponseEntity<UserAccessTokenResponse> responseEntity = loadBalanced.exchange(
                        srlAnalyticsConfig.getExchangeAccessCodeURL(),
                        HttpMethod.POST,
                        entity,
                        UserAccessTokenResponse.class
                );

                userAccessTokenResponse = responseEntity.getBody();
                consumerUuid = userAccessTokenResponse.getUuid();
                UserToken userToken = new UserToken();
                userToken.setFirstName(userAccessTokenResponse.getProfile().getFirstName());
                userToken.setLastName(userAccessTokenResponse.getProfile().getLastName());
                userToken.setConsumerUuid(userAccessTokenResponse.getUuid());
                userToken.setRefreshToken(userAccessTokenResponse.getRefreshToken());
                userToken.setCreationTime(new Date());
                userTokenRepository.save(userToken);

            } catch (HttpClientErrorException e) {
                applicationResponse.setError(e.getMessage());
                applicationResponse.setMessage("Access code expired");
                applicationResponse.setSuccess(false);
                return applicationResponse;
            } catch (RestClientException e) {
                applicationResponse.setError(e.getMessage());
                applicationResponse.setSuccess(false);
                return applicationResponse;
            }
        }
        else{
            try{
                UserToken userToken=userTokenRepository.findFirstByConsumerUuidAndIsDeletedOrderByCreationTimeDesc(formData.get("consumerUuid"),0);
                if(userToken!=null){
                    JSONObject postDataForUserAccessToken = new JSONObject();
                    postDataForUserAccessToken.put("refreshToken",userToken.getRefreshToken());
                    HttpEntity<String> entity = new HttpEntity<String>(postDataForUserAccessToken.toJSONString(), headersForUserAccessToken);
                    ResponseEntity<UserTokenResponse> userTokenResponseResponseEntity = loadBalanced.exchange(
                            srlAnalyticsConfig.getUserTokenURL(),
                            HttpMethod.POST,
                            entity,
                            UserTokenResponse.class
                    );
                    consumerUuid = userToken.getConsumerUuid();
                    userTokenResponse=userTokenResponseResponseEntity.getBody();
                    userToken.setRefreshToken(userTokenResponse.getRefreshToken());
                    userTokenRepository.save(userToken);
                }
                else{
                    applicationResponse.setSuccess(false);
                    applicationResponse.setError("Invalid consumer Uuid");
                    return applicationResponse;
                }
            }
            catch (HttpClientErrorException ex) {
                if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    applicationResponse.setSuccess(false);
                    applicationResponse.setMessage("Refresh token expired or invalid.");
                    applicationResponse.setError(ex.getMessage());
                    return applicationResponse;
                } else {
                    applicationResponse.setSuccess(false);
                    applicationResponse.setError("An unexpected error occurred: " + ex.getMessage());
                    return applicationResponse;
                }

            } catch (Exception ex) {
                // Catch all other exceptions
                applicationResponse.setSuccess(false);
                applicationResponse.setError("Internal server error: " + ex.getMessage());
                return applicationResponse;
            }
        }

        HttpHeaders headersForClientAccessToken = new HttpHeaders();
        headersForClientAccessToken.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formDataForClientAccessToken = new LinkedMultiValueMap<>();
        Optional<LoyaltyOauthCredentials> loyaltyOauthCredentials=loyaltyOauthCredentialsRepository.findByCountry(formData.get("countryCode"));
        if(loyaltyOauthCredentials.isPresent()){
            formDataForClientAccessToken.add("client_id",loyaltyOauthCredentials.get().getClientId());
            formDataForClientAccessToken.add("client_secret",loyaltyOauthCredentials.get().getClientSecret());
            formDataForClientAccessToken.add("grant_type",srlAnalyticsConfig.getOauthGrantType());
        }
        else{
            applicationResponse.setSuccess(false);
            applicationResponse.setError("Invalid country code");
            return applicationResponse;
        }
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formDataForClientAccessToken, headersForClientAccessToken);

        ResponseEntity<ClientAccessTokenResponse> clientAccessTokenResponseEntity = loadBalanced.exchange(
                srlAnalyticsConfig.getClientTokenUrl(),
                HttpMethod.POST,
                requestEntity,
                ClientAccessTokenResponse.class
        );

        System.out.println(clientAccessTokenResponseEntity.getBody());

        ClientAccessTokenResponse clientAccessTokenResponse = clientAccessTokenResponseEntity.getBody();

        Optional<OfferCodeConfiguration> optionalOfferCodeConfiguration = offerCodeConfigurationRepository.findByCountryCodeAndIsDeletedAndEventType(formData.get("countryCode"), 0,Integer.parseInt(formData.get("eventType")));
        if(optionalOfferCodeConfiguration.isPresent()) {
            OfferCodeConfiguration offerCodeConfiguration = optionalOfferCodeConfiguration.get();
            Optional<OfferIssueDetails> optionalOfferIssueDetails = offerIssueDetailsRepository.findFirstByConsumerUuidAndOfferCodeAndIsDeletedAndCountryCodeOrderByCreationTime(consumerUuid,offerCodeConfiguration.getOfferCode(), 0, formData.get("countryCode"));
            if(optionalOfferIssueDetails.isPresent()) {
                applicationResponse.setSuccess(false);
                applicationResponse.setData(optionalOfferIssueDetails.get());
                applicationResponse.setMessage("This offer has already been assigned.");
                applicationResponse.setError("This offer has already been assigned.");
            }else {
                HttpHeaders headersForAssignOffer = new HttpHeaders();
                headersForAssignOffer.setContentType(MediaType.APPLICATION_JSON);
                headersForAssignOffer.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headersForAssignOffer.set("country-code", offerCodeConfiguration.getCountryCode());
                if (formData.get("consumerUuid") != null) {
                    headersForAssignOffer.set("user-authorization-token", "Bearer" + " " + userTokenResponse.getAccessToken());
                    headersForAssignOffer.set("x-correlation-id", formData.get("consumerUuid"));
                } else {
                    headersForAssignOffer.set("user-authorization-token", "Bearer" + " " + userAccessTokenResponse.getAccessToken());
                    headersForAssignOffer.set("x-correlation-id", userAccessTokenResponse.getUuid());
                }
                headersForAssignOffer.set("client-authorization-token", "Bearer" + " " + clientAccessTokenResponse.getAccess_token());
                headersForAssignOffer.set("application", srlAnalyticsConfig.getApplication());
                headersForAssignOffer.set("channel", srlAnalyticsConfig.getChannel());

                JSONObject postDataForAssignOffer = new JSONObject();
                postDataForAssignOffer.put("referenceId", generateReferenceId());

                if (formData.get("consumerUuid") != null) {
                    postDataForAssignOffer.put("consumerUuid", formData.get("consumerUuid"));
                } else {
                    postDataForAssignOffer.put("consumerUuid", userAccessTokenResponse.getUuid());
                }
                postDataForAssignOffer.put("offerId", offerCodeConfiguration.getOfferCode());
                Date validFrom = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                if (offerCodeConfiguration.getValidFrom() != null) {
                    validFrom = offerCodeConfiguration.getValidFrom();
                    postDataForAssignOffer.put("validFrom", offerCodeConfiguration.getValidFrom());
                } else {
                    String validFromStr = standertizeTizeTime(formData.get("validFrom").toString());
                    validFrom = simpleDateFormat.parse(validFromStr);
                    postDataForAssignOffer.put("validFrom", formData.get("validFrom").toString());
                }
                Calendar calendar = Calendar.getInstance();
                if(offerCodeConfiguration.getValidUntil()!=null){
                    Date validTo =  offerCodeConfiguration.getValidUntil();
                    long daysBetween = Duration.between(validFrom.toInstant(), validTo.toInstant()).toDays();

                    int validityPeriod = Integer.parseInt(String.valueOf(daysBetween));
                    postDataForAssignOffer.put("validTo", offerCodeConfiguration.getValidUntil().toString());
                    postDataForAssignOffer.put("validityPeriod", validityPeriod);
                }else {

                    if (offerCodeConfiguration.getValidityPeriod() != null) {
                        postDataForAssignOffer.put("validityPeriod", offerCodeConfiguration.getValidityPeriod());
                    } else {
                        postDataForAssignOffer.put("validityPeriod", Integer.parseInt(formData.get("validityPeriod")));
                    }

                }
                    calendar.setTime(validFrom);
                    Object periodObj = postDataForAssignOffer.get("validityPeriod");
                    int validityPeriod = (periodObj instanceof Integer)
                            ? (Integer) periodObj
                            : Integer.parseInt(periodObj.toString());

                    calendar.add(Calendar.DAY_OF_MONTH, validityPeriod);
                    Date validTo = calendar.getTime();
                    postDataForAssignOffer.put("validTo", simpleDateFormat.format(validTo));

//                if (offerCodeConfiguration.getValidUntil() != null) {
//                    postDataForAssignOffer.put("validTo", offerCodeConfiguration.getValidUntil());
//                } else {
//                    postDataForAssignOffer.put("validTo", formData.get("validTo"));
//                }
                try {
                    String languageCode = formData.get("languageCode").toString();

                    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(srlAnalyticsConfig.getAssignOfferURL())
                            .queryParam("language_code", languageCode);

                    String finalUrl = builder.toUriString();
                    if (postDataForAssignOffer.get("offerId") != null) {
                        HttpEntity<String> entityFromAssignOffer = new HttpEntity<String>(postDataForAssignOffer.toJSONString(), headersForAssignOffer);
                        ResponseEntity<AssignOfferResponse> assignOfferResponseResponse = loadBalanced.exchange(
                                finalUrl,
                                HttpMethod.POST,
                                entityFromAssignOffer,
                                AssignOfferResponse.class
                        );
                    }
                    OfferIssueDetails offerIssueDetails = new OfferIssueDetails();
                    if (formData.get("consumerUuid") != null) {
                        offerIssueDetails.setConsumerUuid(formData.get("consumerUuid"));
                    } else {
                        offerIssueDetails.setConsumerUuid(userAccessTokenResponse.getUuid());
                    }
                    offerIssueDetails.setEventType(offerCodeConfiguration.getEventType());
                    offerIssueDetails.setOfferCode(offerCodeConfiguration.getOfferCode());
                    offerIssueDetails.setCountryCode(offerCodeConfiguration.getCountryCode());
                    offerIssueDetails.setIssuedDate(new Date());
                    if (formData.get("gameId") != null) {
                        offerIssueDetails.setGameId(formData.get("gameId"));
                    }

                    ApplicationResponse applicationResponseFromOfferIssueDetails = this.saveOfferIssueDetails(offerIssueDetails);
                    if (postDataForAssignOffer.get("offerId") == null) {
                        applicationResponse.setData(applicationResponseFromOfferIssueDetails.getData());
                        applicationResponse.setSuccess(false);
                        applicationResponse.setMessage("Offer Code Configuration not found");
                        applicationResponse.setError("Offer Code Configuration not found");
                    } else if (applicationResponseFromOfferIssueDetails.isSuccess()) {
                        applicationResponse.setData(applicationResponseFromOfferIssueDetails.getData());
                        applicationResponse.setSuccess(true);
                        applicationResponse.setError("");
                    } else {
                        applicationResponse.setData(applicationResponseFromOfferIssueDetails.getData());
                        applicationResponse.setSuccess(false);
                        applicationResponse.setError(applicationResponseFromOfferIssueDetails.getError());
                    }
                } catch (Exception e) {
                    String responseBody = e.getMessage();
                    OfferIssueDetails offerIssueDetails = buildOfferIssueDetails(formData, userAccessTokenResponse, offerCodeConfiguration);
                    applicationResponse.setData(offerIssueDetails);
                    if (responseBody.contains("\"errorCode\":422")) {
                        applicationResponse.setMessage(responseBody.split(",")[3].split(":")[1].replace("\"", ""));
                    } else {
                        applicationResponse.setMessage(responseBody);
                    }
                    System.out.println(e.getMessage());
                    applicationResponse.setSuccess(false);
                    applicationResponse.setError(e.getMessage());
                }
            }
        }
        else{
            applicationResponse.setSuccess(false);
            applicationResponse.setMessage("Offer Code Configuration not found");
            applicationResponse.setError("Offer Code Configuration not found");
        }
        return applicationResponse;
    }

    private OfferIssueDetails buildOfferIssueDetails(Map<String, String> formData, UserAccessTokenResponse userAccessTokenResponse, OfferCodeConfiguration offerCodeConfiguration) {

        OfferIssueDetails offerIssueDetails = new OfferIssueDetails();
        if (formData.get("consumerUuid") != null) {
            offerIssueDetails.setConsumerUuid(formData.get("consumerUuid"));
        } else {
            offerIssueDetails.setConsumerUuid(userAccessTokenResponse.getUuid());
        }
        String consumerUuid;
        if(formData.get("consumerUuid")!=null) {
            consumerUuid = formData.get("consumerUuid");
        }
        else{
            consumerUuid = userAccessTokenResponse.getUuid();
        }

        String countryCode = formData.get("countryCode");
        Optional<OfferIssueDetails> optionalOfferIssueDetails=offerIssueDetailsRepository.findFirstByConsumerUuidAndOfferCodeAndIsDeletedAndCountryCodeOrderByCreationTime(consumerUuid,offerCodeConfiguration.getOfferCode(),0,countryCode);
        if(optionalOfferIssueDetails.isPresent()){
            return optionalOfferIssueDetails.get();
        }
        else{
            return null;
        }
    }

    @Override
    public ApplicationResponse getByConsumerUuid(Map<String, String> formData) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {

            String consumerUuid = formData.get("consumerUuid");
            List<OfferCodeConfigurationDTO> offerCodeConfigurationDTOS=offerCodeConfigurationRepository.findAllByIsDeletedAndCountryCode(formData.get("countryCode"));
//            for (OfferCodeConfigurationDTO dto : offerCodeConfigurationDTOS) {
//                String eventTypeText = EventTypeUtil.getEventText(dto.getEventType());
//                List<CodeConfigurationTranslation> codeConfigurationTranslations= codeConfigurationTranslationRepository.findAllByIsDeletedAndLangCodeAndName(0,formData.get("langCode"),dto.getPoints());
//                if(codeConfigurationTranslations.size()>0) {
//                    String translatedText = codeConfigurationTranslations.get(0).getTranslatedName();
//                    if (translatedText != null) {
//                        dto.setPoints(translatedText);
//                    }
//                }

//                dto.setEventTypeText(eventTypeText);
//            }

            List<OfferIssueDetailsDTO> offerCodeConfigurationList=new ArrayList<>();
            if(formData.get("countryCode")!=null){
                offerCodeConfigurationList=offerIssueDetailsRepository.findAllByConsumerUuidAndIsDeleted(consumerUuid,0,formData.get("countryCode"));
            }

            for(OfferCodeConfigurationDTO dto:offerCodeConfigurationDTOS){

                for(OfferIssueDetailsDTO offerIssueDetailsDTO:offerCodeConfigurationList) {
                    if (offerIssueDetailsDTO.getOfferCode()!=null &&offerIssueDetailsDTO.getOfferCode().equals(dto.getOfferCode()) && offerIssueDetailsDTO.getEventType().equals(dto.getEventType()) && offerIssueDetailsDTO.getCountryCode().equals(dto.getCountryCode())) {
                        dto.setGameId(offerIssueDetailsDTO.getGameId());
                        dto.setClaimed(true);
                    }
                    if(offerIssueDetailsDTO.getOfferCode()==null && offerIssueDetailsDTO.getEventType().equals(dto.getEventType()) && offerIssueDetailsDTO.getCountryCode().equals(dto.getCountryCode())) {
                        dto.setGameId(offerIssueDetailsDTO.getGameId());
                        dto.setClaimed(true);
                    }
                }
            }

            applicationResponse.setData(offerCodeConfigurationDTOS);
            applicationResponse.setSuccess(true);
            applicationResponse.setError("");

        } catch (Exception e) {
            applicationResponse.setSuccess(false);
            applicationResponse.setError(e.getMessage());
        }
        return applicationResponse;
    }

    public static String generateReferenceId() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
    }

    private TokenResponse getDigestToken() throws Exception {

        String clientId = srlAnalyticsConfig.getSsoClientId();
        String digest = DigestUtils.sha256Hex(clientId.getBytes(StandardCharsets.UTF_8));

        Map<String, String> requestBody = Map.of("digest", digest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<TokenResponse> response = loadBalanced.exchange(
                srlAnalyticsConfig.getBaseTokenURL(),
                HttpMethod.POST,
                request,
                TokenResponse.class
        );
        return response.getBody();
    }

    public static String standertizeTizeTime(String input) {
        try {
            // Handles both with or without fractional seconds
            Instant instant = Instant.parse(input);
            return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .withZone(java.time.ZoneOffset.UTC)
                    .format(instant);
        } catch (DateTimeParseException e) {
            // Optional fallback if input is missing timezone or invalid
            try {
                OffsetDateTime odt = OffsetDateTime.parse(input, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        .withZone(java.time.ZoneOffset.UTC)
                        .format(odt.toInstant());
            } catch (Exception ex) {
                throw new RuntimeException("Invalid date: " + input);
            }
        }
    }
}
