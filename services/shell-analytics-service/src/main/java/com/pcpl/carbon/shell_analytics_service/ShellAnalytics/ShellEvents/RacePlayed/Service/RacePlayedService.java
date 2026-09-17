package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RacePlayed.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;

import java.util.Map;

public interface RacePlayedService {
    ApplicationResponse getCountryWiseData(Map<String, String> formData)throws Exception;
    ApplicationResponse getCountryWiseCompetitionData(Map<String, String> formData)throws Exception;

}
