package com.pcpl.carbon.common_service.Leaderboards.Service;

import com.pcpl.carbon.commonservice.Config.CRConfig;
import com.pcpl.carbon.common_service.Leaderboards.Repository.LeaderboardsRepository;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.Leaderboards.DTO.LeaderboardsDTO;
import com.pcpl.carbon.pcplsdk.Leaderboards.DTO.LoginRequestDTO;
import com.pcpl.carbon.pcplsdk.Leaderboards.Model.Leaderboards;
import com.pcpl.carbon.pcplsdk.Leaderboards.Response.LoginResponse;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LeaderboardsServiceImpl extends AbstractLazyService<Leaderboards, LeaderboardsDTO, LeaderboardsRepository> implements LeaderboardsService {


    @Autowired
    RestTemplate loadBalanced;

    @Autowired
    CRConfig crConfig;

    @Override
    public ApplicationResponse get(String statisticName, Integer startPosition) throws Exception {
        try {
            String entityToken = getEntityToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.set("X-EntityToken", entityToken );
            JSONObject postData = new JSONObject();
            postData.put("statisticName", statisticName);
            postData.put("startPosition", startPosition);
            HttpEntity<String> entity = new HttpEntity<String>(postData.toJSONString(), headers);
            ResponseEntity<ApplicationResponse> result = loadBalanced.exchange(
                    crConfig.getPlayFabURL() + "/GetLeaderboard",
                    HttpMethod.POST,
                    entity,
                    ApplicationResponse.class
            );

            return result.getBody();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private String getEntityToken() throws Exception {

        LoginRequestDTO loginRequest = new LoginRequestDTO("222", true, "18B72", null);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequestDTO> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<LoginResponse> response = loadBalanced.exchange(
                crConfig.getPlayFabURL() + "/LoginWithCustomID",
                HttpMethod.POST,
                request,
                LoginResponse.class
        );
        System.out.println(response);
        return String.valueOf(response.getBody().getData().getEntityToken().getEntityToken());
    }


    @Override
    public Leaderboards getEntityObject() {
        return new Leaderboards();
    }

    @Override
    public LeaderboardsDTO getDtoObject() {
        return new LeaderboardsDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }
}