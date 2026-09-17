package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferIssueDetails.Controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.OfferIssueDetailsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.ShellEventsDataDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.OfferCodeConfiguration;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.OfferIssueDetails;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferCodeConfiguration.Service.OfferCodeConfigurationService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferIssueDetails.Repository.OfferIssueDetailsRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferIssueDetails.Service.OfferIssueDetailsService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferIssueDetails.Service.OfferIssueDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/srl/offer")
@Slf4j
public class OfferIssueDetailsController extends AbstractCRUDController<OfferIssueDetails, OfferIssueDetailsDTO, OfferIssueDetailsRepository, OfferIssueDetailsServiceImpl> {

    @Autowired
    OfferIssueDetailsService offerIssueDetailsService;

    @RequestMapping(value = "/send-offer", method = RequestMethod.POST)
    public ApplicationResponse sendOffer(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        try{
            applicationResponse = offerIssueDetailsService.sendOffers(formData);
        }catch (Exception e){
            applicationResponse.setError(e.getMessage());
            applicationResponse.setSuccess(false);
        }
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }


    @RequestMapping(value = "/get-by-consumer-uuid", method = RequestMethod.POST)
    public ApplicationResponse getByConsumerUuid(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = offerIssueDetailsService.getByConsumerUuid(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
