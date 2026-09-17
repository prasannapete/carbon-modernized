package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.AppLaunchedDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface AppLaunchedService {
    ApplicationResponse getCountryWiseData(Map<String, String> formData)throws Exception;
}
