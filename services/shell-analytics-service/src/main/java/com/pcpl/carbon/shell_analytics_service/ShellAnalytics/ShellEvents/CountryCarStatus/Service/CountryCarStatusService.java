package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CountryCarStatus.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryCarStatusDTO;

public interface CountryCarStatusService {
    ApplicationResponse saveAndUpdateCountryCarStatus(CountryCarStatusDTO countryCarStatusDTO)throws Exception;

}
