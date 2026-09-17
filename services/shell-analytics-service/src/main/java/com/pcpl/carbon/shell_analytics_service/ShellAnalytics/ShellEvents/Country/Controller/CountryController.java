package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Country.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.Country;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Country.Repository.CountryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Country.Service.CountryService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Country.Service.CountryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/srl/country")
@Slf4j
public class CountryController extends AbstractCRUDController<Country, CountryDTO, CountryRepository, CountryServiceImpl> {

    @Autowired
    private CountryService countryService;

    @RequestMapping(value = "/get-countries", method = RequestMethod.POST)
    public ApplicationResponse getCountries() throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = countryService.getCountries();
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

    @RequestMapping(value = "/get-countries-by-user", method = RequestMethod.POST)
    public ApplicationResponse getCountriesByUser() throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = countryService.getCountriesByUser();
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
