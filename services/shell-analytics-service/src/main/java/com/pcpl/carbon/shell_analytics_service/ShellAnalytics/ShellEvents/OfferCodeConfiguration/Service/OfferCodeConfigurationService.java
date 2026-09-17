package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferCodeConfiguration.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;

import java.util.Map;

public interface OfferCodeConfigurationService {
    ApplicationResponse getByEventTypeAndCountryCode(Map<String, String> formData)throws Exception;

    ApplicationResponse getByCountryCode(Map<String, String> formData)throws Exception;
}
