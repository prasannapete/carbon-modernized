package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.AppLaunchedSummary.Model.AppLaunchedSummary;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.AppLaunchedDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryWiseAppLaunchStatsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryWiseAppLaunchedSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.AppLaunched;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.Model.UserCountries;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.AppLaunchedSummary.Repository.AppLaunchedSummaryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.AppLaunchedSummary.Service.AppLaunchedSummaryService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Repository.AppLaunchedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service.UserInformationService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository.UserCountriesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class AppLaunchedServiceImpl extends AbstractLazyService<AppLaunched, AppLaunchedDTO, AppLaunchedRepository> implements AppLaunchedService{
    @Override
    public AppLaunched getEntityObject() {
        return new AppLaunched();
    }

    @Override
    public AppLaunchedDTO getDtoObject() {
        return new AppLaunchedDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getGameId";
    }

    @Override
    public AppLaunchedDTO save(@RequestBody AppLaunchedDTO appLaunchedDTO) {
            return super.save(appLaunchedDTO);
    }

    @Autowired
    AppLaunchedRepository appLaunchedRepository;

    @Autowired
    UserInformationService userInformationService;

    @Autowired
    UserCountriesRepository userCountriesRepository;

    @Override
    public ApplicationResponse getCountryWiseData(Map<String, String> formData)throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        User user = userInformationService.getUser();
        try{
            String startDateStr = formData.get("startDate");
            String endDateStr = formData.get("endDate");
            String countries =  formData.get("country");
            List<String> countryList = new ArrayList<>();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = formatter.parse(startDateStr);
            Date endDate = formatter.parse(endDateStr);
            if (countries != null && !countries.trim().isEmpty()) {
                countryList = Arrays.stream(countries.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
            }
            if (countryList.isEmpty()) {
                List<UserCountriesDTO> userCountriesDTOS = userCountriesRepository.getUserCountriesByUserId(user.getId());
                countryList = userCountriesDTOS.stream().map(UserCountriesDTO::getCountryCode).toList();
            }
            List<CountryWiseAppLaunchStatsDTO> stats = appLaunchedRepository.getCountryWiseLaunchesBetweenDates(startDate, endDate,countryList);

            // Group by country
            Map<String, List<Map<String, Object>>> countryWiseData = new HashMap<>();
            for (CountryWiseAppLaunchStatsDTO stat : stats) {
                countryWiseData.computeIfAbsent(stat.getCountry(), k -> new ArrayList<>()).add(
                        Map.of(
                                "date", formatter.format(stat.getDate()),
                                "count", stat.getCount()
                        )
                );
            }
            List<Object[]> summaryRaw = appLaunchedRepository.getCountryWiseLaunchedSummary(startDate, endDate,countryList);

            List<CountryWiseAppLaunchedSummaryDTO> summaryTable = new ArrayList<>();
            long totalLaunches = 0;
            long totalCumulative = 0;

            for (Object[] row : summaryRaw) {
                String country = (String) row[0];
                Long launches = ((Number) row[1]).longValue();
                Long cumulative = ((Number) row[2]).longValue();
                String countryCode = (String) row[3];

                summaryTable.add(new CountryWiseAppLaunchedSummaryDTO(country, launches, cumulative,countryCode));
                totalLaunches += launches;
                totalCumulative += cumulative;
            }

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("countryWiseGraphData", countryWiseData);
            responseMap.put("summaryTable", summaryTable);
            responseMap.put("grandTotal", Map.of("launches", totalLaunches, "cumulative", totalCumulative));
            applicationResponse.setData(responseMap);
            applicationResponse.setSuccess(true);
            applicationResponse.setError("");
        }
        catch (Exception e) {
            applicationResponse.setSuccess(false);
            applicationResponse.setError(e.getMessage());
        }
        return applicationResponse;
    }
}
