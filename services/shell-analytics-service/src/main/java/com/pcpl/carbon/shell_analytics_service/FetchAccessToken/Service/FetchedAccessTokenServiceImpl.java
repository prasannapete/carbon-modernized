package com.pcpl.carbon.shell_analytics_service.FetchAccessToken.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcpl.carbon.pcplsdk.ClientDetails.Model.ClientDetails;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.shell_analytics_service.ClientDetails.Repository.ClientDetailsRepository;
import com.pcpl.carbon.shell_analytics_service.config.SrlAnalyticsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/srl")
@Slf4j
@Component
public class FetchedAccessTokenServiceImpl {

    @Autowired
    SrlAnalyticsConfig srlAnalyticsConfig;

    @Autowired
    RestTemplate clientAuthenticated;

    @Autowired
    ClientDetailsRepository clientDetailsRepository;

    private static final String GRANT_TYPE = "client_credentials";

    @RequestMapping(value = "/get-access-token", method = RequestMethod.POST)
    public String getEventData(@RequestBody String countryCode) {
        Optional<ClientDetails> clientDetails = clientDetailsRepository.findByCountryCodeAndIsDeleted(countryCode, 0);
        String clientId = "";
        String clientSecret = "";
        if(clientDetails.isPresent()) {
            clientId = clientDetails.get().getClientId();
            clientSecret = clientDetails.get().getClientSecret();
        }

        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        RestTemplate restTemplate = new RestTemplate();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret); // Encodes client ID and secret

        // Set body
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // Send request
//        ResponseEntity<Map> response = restTemplate.postForEntity(srlAnalyticsConfig.getAccessTokenUrl(), requestEntity, Map.class);
        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> entity = new HttpEntity<String>("", headers);
        ResponseEntity<Map> response = clientAuthenticated.exchange(
                srlAnalyticsConfig.getAccessTokenUrl(),
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("access_token")) {
                return (String) responseBody.get("access_token");
            }
        }


        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return null;
    }

    @RequestMapping(value = "/get-access-token-2", method = RequestMethod.POST)
    public String getAccessToken(@RequestBody Map<String,String> formData) {
        Optional<ClientDetails> clientDetails = clientDetailsRepository.findByCountryCodeAndIsDeleted(formData.get("countryCode"), 0);
        String clientId = "";
        String clientSecret = "";
        if(clientDetails.isPresent()) {
            clientId = clientDetails.get().getClientId();
            clientSecret = clientDetails.get().getClientSecret();
        }
        WebClient webClient = WebClient.builder()
                .baseUrl(srlAnalyticsConfig.accessTokenUrl)
                .build();
        String response = null;
        if(clientDetails.isPresent()) {
             response = webClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(UriComponentsBuilder.newInstance()
                            .queryParam("grant_type", GRANT_TYPE)
                            .queryParam("client_id", clientId)
                            .toUriString().substring(1))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }
        return extractAccessToken(response);
    }

    private static String extractAccessToken(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
