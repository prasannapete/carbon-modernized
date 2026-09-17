package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RCPlayed.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.RCPlayedDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.RCPlayed;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Service.AppLaunchedService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RCPlayed.Repository.RCPlayedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RCPlayed.Service.RCPlayedService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RCPlayed.Service.RCPlayedServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/shell-events/rc-played")
@Slf4j
public class RCPlayedController extends AbstractCRUDController<RCPlayed, RCPlayedDTO, RCPlayedRepository, RCPlayedServiceImpl> {

    @Autowired
    RCPlayedService rcPlayedService;

    @RequestMapping(value = "/get-country-wise-data", method = RequestMethod.POST)
    public ApplicationResponse getByConsumerUuid(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = rcPlayedService.getCountryWiseData(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
