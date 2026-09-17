package com.pcpl.carbon.shell_analytics_service.PlayStoreReport.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;

import java.util.Map;

public interface PlayStoreReportService {
    ApplicationResponse fetchPlayStoreSalesReport(Map<String,String> formData) throws Exception;
}

