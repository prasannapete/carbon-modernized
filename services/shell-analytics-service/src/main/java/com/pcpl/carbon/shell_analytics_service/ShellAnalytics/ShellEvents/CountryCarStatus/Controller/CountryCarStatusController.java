package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CountryCarStatus.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryCarStatusDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.CountryCarStatus;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CountryCarStatus.Repository.CountryCarStatusRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CountryCarStatus.Service.CountryCarStatusService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CountryCarStatus.Service.CountryCarStatusServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/srl/country-car-status")
@Slf4j
public class CountryCarStatusController extends AbstractCRUDController<CountryCarStatus, CountryCarStatusDTO, CountryCarStatusRepository, CountryCarStatusServiceImpl> {
    @Autowired
    private CountryCarStatusService countryCarStatusService;

    @RequestMapping(value = "/save-car-status", method = RequestMethod.POST)
    public ApplicationResponse saveCarStatus(@RequestBody CountryCarStatusDTO countryCarStatusDTO) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = countryCarStatusService.saveAndUpdateCountryCarStatus(countryCarStatusDTO);
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
