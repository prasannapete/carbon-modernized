package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.BrandViewedGarage.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.BrandViewedGarageDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryWiseBrandViewsStatsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryWiseBrandViewsSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.BrandViewedGarage;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.BrandViewedGarage.Repository.BrandViewedGarageRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service.UserInformationService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository.UserCountriesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class BrandViewedGarageServiceImpl extends AbstractLazyService<BrandViewedGarage, BrandViewedGarageDTO, BrandViewedGarageRepository> implements BrandViewedGarageService {
    @Override
    public BrandViewedGarage getEntityObject() {
        return new BrandViewedGarage();
    }

    @Override
    public BrandViewedGarageDTO getDtoObject() {
        return new BrandViewedGarageDTO();
    }
    
    @Autowired
    BrandViewedGarageRepository brandViewedGarageRepository;

    @Autowired
    UserInformationService userInformationService;

    @Autowired
    UserCountriesRepository userCountriesRepository;

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getBrandCount";
    }

    @Override
    public BrandViewedGarageDTO save(@RequestBody BrandViewedGarageDTO brandViewedGarageDTO) {
        return super.save(brandViewedGarageDTO);
    }
    

    @Override
    public ApplicationResponse getCountryWiseData(Map<String, String> formData) throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        try{
            User user = userInformationService.getUser();
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
            List<CountryWiseBrandViewsStatsDTO> stats = brandViewedGarageRepository.getCountryWiseViewsBetweenDates(startDate, endDate,countryList);

            // Group by country
            Map<String, List<Map<String, Object>>> countryWiseData = new HashMap<>();
            for (CountryWiseBrandViewsStatsDTO stat : stats) {
                countryWiseData.computeIfAbsent(stat.getCountry(), k -> new ArrayList<>()).add(
                        Map.of(
                                "date", formatter.format(stat.getDate()),
                                "count", stat.getCount()
                        )
                );
            }
            List<Object[]> summaryRaw = brandViewedGarageRepository.getCountryWiseViewsSummary(startDate, endDate,countryList);

            List<CountryWiseBrandViewsSummaryDTO> summaryTable = new ArrayList<>();
            long totalViews = 0;
            long totalCumulative = 0;

            for (Object[] row : summaryRaw) {
                String country = (String) row[0];
                Long views = ((Number) row[1]).longValue();
                Long cumulative = ((Number) row[2]).longValue();
                String countryName = (String) row[3];
                summaryTable.add(new CountryWiseBrandViewsSummaryDTO(country, views, cumulative,countryName));
                totalViews += views;
                totalCumulative += cumulative;
            }

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("countryWiseGraphData", countryWiseData);
            responseMap.put("summaryTable", summaryTable);
            responseMap.put("grandTotal", Map.of("views", totalViews, "cumulative", totalCumulative));
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
