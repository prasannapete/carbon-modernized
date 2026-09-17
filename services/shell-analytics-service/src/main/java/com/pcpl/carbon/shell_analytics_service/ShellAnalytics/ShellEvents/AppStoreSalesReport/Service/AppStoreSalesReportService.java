package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppStoreSalesReport.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;

import java.util.Map;

public interface AppStoreSalesReportService {
    ApplicationResponse fetchAppStoreSalesReport(Map<String,String> formData) throws Exception;
}
