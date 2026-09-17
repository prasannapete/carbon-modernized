package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.BrandViewedGarage.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;

import java.util.Map;

public interface BrandViewedGarageService {
    ApplicationResponse getCountryWiseData(Map<String, String> formData)throws Exception;
}
