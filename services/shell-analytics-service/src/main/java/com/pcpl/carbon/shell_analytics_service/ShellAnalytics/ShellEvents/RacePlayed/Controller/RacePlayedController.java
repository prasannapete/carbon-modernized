package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RacePlayed.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.RacePlayedDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.RacePlayed;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RacePlayed.Repository.RacePlayedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RacePlayed.Service.RacePlayedServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/shell-events/race-played")
@Slf4j
public class RacePlayedController extends AbstractCRUDController<RacePlayed, RacePlayedDTO, RacePlayedRepository, RacePlayedServiceImpl> {
    @Autowired
    RacePlayedServiceImpl racePlayedService;

    @RequestMapping(value = "/get-country-wise-data", method = RequestMethod.POST)
    public ApplicationResponse getByConsumerUuid(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = racePlayedService.getCountryWiseData(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }@RequestMapping(value = "/get-country-wise-competition-data", method = RequestMethod.POST)
    public ApplicationResponse getCompetitionData(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = racePlayedService.getCountryWiseCompetitionData(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
