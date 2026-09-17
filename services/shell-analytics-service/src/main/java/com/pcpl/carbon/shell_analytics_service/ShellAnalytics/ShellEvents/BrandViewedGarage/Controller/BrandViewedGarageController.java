package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.BrandViewedGarage.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.BrandViewedGarageDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.BrandViewedGarage;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Service.AppLaunchedService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.BrandViewedGarage.Repository.BrandViewedGarageRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.BrandViewedGarage.Service.BrandViewedGarageService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.BrandViewedGarage.Service.BrandViewedGarageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/shell-events/brand-viewed-garage")
@Slf4j
public class BrandViewedGarageController extends AbstractCRUDController<BrandViewedGarage, BrandViewedGarageDTO, BrandViewedGarageRepository, BrandViewedGarageServiceImpl> {

    @Autowired
    BrandViewedGarageService brandViewedGarageService;

    @RequestMapping(value = "/get-country-wise-data", method = RequestMethod.POST)
    public ApplicationResponse getByConsumerUuid(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = brandViewedGarageService.getCountryWiseData(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
