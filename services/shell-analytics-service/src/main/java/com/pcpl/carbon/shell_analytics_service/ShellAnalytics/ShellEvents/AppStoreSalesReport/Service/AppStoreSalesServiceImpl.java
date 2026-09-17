package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppStoreSalesReport.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.AppStoreSalesReportDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.AppStoreSalesReportGraphDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.AppStoreSalesReport;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppStoreSalesReport.Repository.AppStoreSalesReportRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service.UserInformationService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository.UserCountriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppStoreSalesServiceImpl extends AbstractLazyService<AppStoreSalesReport, AppStoreSalesReportDTO, AppStoreSalesReportRepository> implements AppStoreSalesReportService {
@Override
    public AppStoreSalesReport getEntityObject(){ return new AppStoreSalesReport();}
    @Override
    public AppStoreSalesReportDTO getDtoObject(){ return new AppStoreSalesReportDTO();}
    @Override
    public String getUniqueConstraintCheckMethodName(){ return "getId";}

    @Autowired
    UserInformationService userInformationService;

    @Autowired
    UserCountriesRepository userCountriesRepository;

    @Autowired
    AppStoreSalesReportRepository appStoreSalesReportRepository;
    @Override
    public ApplicationResponse fetchAppStoreSalesReport(Map<String, String> formData) throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        User user = userInformationService.getUser();
        try {
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
            List<AppStoreSalesReportGraphDTO> rawStats = appStoreSalesReportRepository.getAppSoreSalesReportByDateRange(startDate, endDate,countryList);
            for(AppStoreSalesReportGraphDTO appStoreSalesReportGraphDTO : rawStats){
                if(appStoreSalesReportGraphDTO.getMonth()!=null) {
                    Month month = Month.of(appStoreSalesReportGraphDTO.getMonth());
                    appStoreSalesReportGraphDTO.setMonthName(month.name());
                }
            }
            Map<String, List<AppStoreSalesReportGraphDTO>> graphGroupedByCountry = rawStats.stream()
                    .collect(Collectors.groupingBy(AppStoreSalesReportGraphDTO::getCountry));
            Map<String, Object> responsePayload = new HashMap<>();
            responsePayload.put("graphData", graphGroupedByCountry);

            applicationResponse.setSuccess(true);
            applicationResponse.setData(responsePayload);

        }catch (Exception e){
            applicationResponse.setSuccess(false);
            applicationResponse.setError(e.getMessage());
        }
        return applicationResponse;
    }
}
