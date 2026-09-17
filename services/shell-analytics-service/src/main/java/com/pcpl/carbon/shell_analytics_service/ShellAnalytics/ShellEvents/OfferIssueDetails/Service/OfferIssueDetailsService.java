package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferIssueDetails.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.OfferIssueDetailsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.OfferIssueDetails;

import java.util.Map;

public interface OfferIssueDetailsService {


    ApplicationResponse saveOfferIssueDetails(OfferIssueDetails offerIssueDetails);

    ApplicationResponse sendOffers(Map<String, String> formData) throws Exception;

    ApplicationResponse getByConsumerUuid(Map<String, String> formData);
}
