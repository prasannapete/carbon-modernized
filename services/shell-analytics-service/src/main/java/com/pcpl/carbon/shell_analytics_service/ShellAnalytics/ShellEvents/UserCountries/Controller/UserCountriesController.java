package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Controller;

import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.Model.UserCountries;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository.UserCountriesRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Service.UserCountriesServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user-countries")
@Slf4j
public class UserCountriesController extends AbstractCRUDController<UserCountries, UserCountriesDTO, UserCountriesRepository, UserCountriesServiceImpl> {
}
