package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RCPlayedSummary.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;

import java.util.Date;
import java.util.Map;

public interface RCPlayedSummaryService {
    ApplicationResponse getSummaryByDate(String givenDate);
    void generateEndOfDaySummaryForCountry(String country, Date summaryDate);
    Map<String, Object> getSummaryByDateRange(String startDate, String endDate, String country) throws Exception;
    void migrateRCPlayedData(String startDateStr, String endDateStr) throws Exception;
}
