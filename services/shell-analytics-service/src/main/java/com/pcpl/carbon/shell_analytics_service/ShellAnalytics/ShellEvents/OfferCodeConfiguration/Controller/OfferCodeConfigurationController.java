package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferCodeConfiguration.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.OfferCodeConfigurationDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.OfferCodeConfiguration;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferCodeConfiguration.Repository.OfferCodeConfigurationRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferCodeConfiguration.Service.OfferCodeConfigurationService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferCodeConfiguration.Service.OfferCodeConfigurationServiceImpl;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferIssueDetails.Service.OfferIssueDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/offer-code-configuration")
@Slf4j
public class OfferCodeConfigurationController extends AbstractCRUDController<OfferCodeConfiguration, OfferCodeConfigurationDTO, OfferCodeConfigurationRepository, OfferCodeConfigurationServiceImpl> {

    @Autowired
    OfferCodeConfigurationService offerCodeConfigurationService;

    @RequestMapping(value = "/get-by-event-type-and-country-code", method = RequestMethod.POST)
    public ApplicationResponse getByEventTypeAndCountryCode(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = offerCodeConfigurationService.getByEventTypeAndCountryCode(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

    @RequestMapping(value = "/get-by-country-code", method = RequestMethod.POST)
    public ApplicationResponse getByCountryCode(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = offerCodeConfigurationService.getByCountryCode(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
