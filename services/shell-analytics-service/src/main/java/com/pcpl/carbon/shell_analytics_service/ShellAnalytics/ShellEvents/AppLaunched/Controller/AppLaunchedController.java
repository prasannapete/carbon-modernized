package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.AppLaunchedDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.AppLaunched;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Repository.AppLaunchedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Service.AppLaunchedService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Service.AppLaunchedServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/shell-events/app-launched")
@Slf4j
public class AppLaunchedController extends AbstractCRUDController<AppLaunched, AppLaunchedDTO, AppLaunchedRepository, AppLaunchedServiceImpl> {

    @Autowired
    AppLaunchedService appLaunchedService;

    @RequestMapping(value = "/get-country-wise-data", method = RequestMethod.POST)
    public ApplicationResponse getByConsumerUuid(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = appLaunchedService.getCountryWiseData(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
