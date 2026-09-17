package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.AppLaunchedSummary.Service;


import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import java.util.Date;
import java.util.Map;


public interface AppLaunchedSummaryService {
    ApplicationResponse getSummaryByDate(String givenDate);
    void generateEndOfDaySummaryForCountry(String country, Date summaryDate);
    Map<String, Object> getSummariesByUserCountries(String countries, String startDate, String endDate) throws Exception;
    void migrateAppLaunchData(String startDateStr, String endDateStr) throws Exception;
}

