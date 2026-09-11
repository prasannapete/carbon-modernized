package com.pcpl.carbon.common_service.Leaderboards.Controller;

import com.pcpl.carbon.common_service.Leaderboards.Repository.LeaderboardsRepository;
import com.pcpl.carbon.common_service.Leaderboards.Service.LeaderboardsService;
import com.pcpl.carbon.common_service.Leaderboards.Service.LeaderboardsServiceImpl;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.Leaderboards.DTO.LeaderboardsDTO;
import com.pcpl.carbon.pcplsdk.Leaderboards.Model.Leaderboards;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@Controller
@RequestMapping(value = "/leaderboard")
public class LeaderboardsController extends AbstractCRUDController<Leaderboards, LeaderboardsDTO, LeaderboardsRepository, LeaderboardsServiceImpl> {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardsController.class);

    @Autowired
    LeaderboardsService leaderboardService;

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public @ResponseBody
    ApplicationResponse get(@RequestBody Map<String, String> formData) {
        logger.trace("Entering");
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            applicationResponse = leaderboardService.get(formData.get("StatisticName"), Integer.parseInt(formData.get("StartPosition")));
            applicationResponse.setSuccess(true);
            applicationResponse.setError("");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            applicationResponse.setSuccess(false);
            applicationResponse.setError(ex.getMessage());
        }
        logger.trace("Exiting");
        return applicationResponse;
    }

}
