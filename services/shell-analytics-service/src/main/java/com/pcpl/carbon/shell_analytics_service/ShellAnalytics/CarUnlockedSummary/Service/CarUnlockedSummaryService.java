package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.CarUnlockedSummary.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;

import java.util.Date;
import java.util.Map;

public interface CarUnlockedSummaryService {
    ApplicationResponse getSummaryByDate(String givenDate);
    void generateEndOfDaySummaryForCountry(String country, Date summaryDate);
    Map<String, Object> getSummaryByDateRange(String startDateStr, String endDateStr, String country) throws Exception;
    void migrateCarUnlockedData(String startDateStr, String endDateStr) throws Exception;
}
