package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Country.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.Country;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.Model.UserCountries;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Repository.AppLaunchedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Country.Repository.CountryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service.UserInformationService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository.UserCountriesRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CountryServiceImpl extends AbstractLazyService<Country, CountryDTO, CountryRepository> implements CountryService{
    @Override
    public Country getEntityObject() {
        return new Country();
    }

    @Override
    public CountryDTO getDtoObject() {
        return new CountryDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }
    @Autowired
    AppLaunchedRepository appLaunchedRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    UserInformationService userInformationService;

    @Autowired
    UserCountriesRepository userCountriesRepository;

    @Override
    public ApplicationResponse getCountries() throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            List<Country> countries = countryRepository.findAllByIsDeletedOrderByCountryName(0);
            applicationResponse.setData(countries);
            applicationResponse.setSuccess(true);
            applicationResponse.setError(null);
        } catch (Exception e) {
            applicationResponse.setError(e.getMessage());
        }
        return applicationResponse;}

    @Override
    public ApplicationResponse getCountriesByUser() throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        User user = userInformationService.getUser();
        try {
            List<Long> userCountries = userCountriesRepository.getCountryIdsByUserId(user.getId());
            List<Country> countries = countryRepository.findAllByIsDeletedOrderByCountryName(0);
            List<Country> countryList = new ArrayList<>();
            for (Country country : countries) {
                if (userCountries.contains(country.getId())) {
                    countryList.add(country);
                }
            }
            countryList.sort(Comparator.comparing(Country::getCountryName));
            applicationResponse.setData(countryList);
            applicationResponse.setSuccess(true);
            applicationResponse.setError(null);
        } catch (Exception e) {
            applicationResponse.setError(e.getMessage());
        }
        return applicationResponse;
    }
}
