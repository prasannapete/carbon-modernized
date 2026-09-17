package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RacePlayedSummary.Service;


import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;

import java.util.Date;
import java.util.Map;

public interface RacePlayedSummaryService {
    ApplicationResponse getSummaryByDate(String givenDate);
    void generateEndOfDaySummaryForCountry(String country, Date summaryDate);
    Map<String, Object> getSummaryByDateRange(String startDate, String endDate, String country) throws Exception;
    void migrateRacePlayedData(String startDateStr, String endDateStr) throws Exception;
}
