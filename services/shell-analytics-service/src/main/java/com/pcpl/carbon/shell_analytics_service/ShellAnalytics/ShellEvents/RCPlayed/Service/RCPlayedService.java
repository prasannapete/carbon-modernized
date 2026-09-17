package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RCPlayed.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;

import java.util.Map;

public interface RCPlayedService {
    ApplicationResponse getCountryWiseData(Map<String, String> formData)throws Exception;
}
