package com.pcpl.carbon.shell_analytics_service.MasterCars.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.MasterCars.DTO.MasterCarsDTO;

import java.util.Map;

public interface MasterCarsService {
    ApplicationResponse getCarUnlockedData(Map<String, String> formData)throws Exception;

    ApplicationResponse updateCarStatus(MasterCarsDTO masterCarsDTO) throws Exception;

}
