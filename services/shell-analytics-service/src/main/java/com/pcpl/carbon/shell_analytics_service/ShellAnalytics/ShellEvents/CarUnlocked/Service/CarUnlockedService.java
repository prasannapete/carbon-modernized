package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CarUnlocked.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;

import java.util.Map;

public interface CarUnlockedService {
    ApplicationResponse getCarUnlockedData(Map<String, String> formData)throws Exception;
    ApplicationResponse getCarUnlockedDataByCountry(Map<String, String> formData)throws Exception;
    ApplicationResponse getCountryWiseAverages(Map<String, String> formData)throws Exception;
}
