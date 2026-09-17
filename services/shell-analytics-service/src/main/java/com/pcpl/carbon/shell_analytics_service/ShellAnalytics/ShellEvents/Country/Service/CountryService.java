package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Country.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;

import java.util.Map;

public interface CountryService {
    ApplicationResponse getCountries()throws Exception;
    ApplicationResponse getCountriesByUser()throws Exception;
}
