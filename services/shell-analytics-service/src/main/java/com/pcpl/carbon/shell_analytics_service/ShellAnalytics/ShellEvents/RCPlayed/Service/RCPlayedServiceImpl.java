package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RCPlayed.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.RCPlayedDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.RemoteControlPlayedGraphDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.RemoteControlPlayedSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.RCPlayed;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RCPlayed.Repository.RCPlayedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service.UserInformationService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository.UserCountriesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RCPlayedServiceImpl extends AbstractLazyService<RCPlayed, RCPlayedDTO, RCPlayedRepository> implements RCPlayedService {
    @Override
    public RCPlayed getEntityObject() {
        return new RCPlayed();
    }

    @Override
    public RCPlayedDTO getDtoObject() {
        return new RCPlayedDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getCarName";
    }

    @Autowired
    RCPlayedRepository rcPlayedRepository;

    @Autowired
    UserInformationService userInformationService;

    @Autowired
    UserCountriesRepository userCountriesRepository;

    @Override
    public RCPlayedDTO save(@RequestBody RCPlayedDTO rcPlayedDTO) {
        return super.save(rcPlayedDTO);
    }

    @Override
    public ApplicationResponse getCountryWiseData(Map<String, String> formData) throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        try {
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
            List<RemoteControlPlayedGraphDTO> rawStats =
                    rcPlayedRepository.getRemoteControlPlayedStatsByDateRange(startDate, endDate,countryList);

            List<Object[]> filteredCumulativeRaw =
                    rcPlayedRepository.getTotalCumulativeCounts(countryList);

            Map<String, Double> filteredCumulativeMap = filteredCumulativeRaw.stream()
                    .collect(Collectors.toMap(
                            row -> row[0] + "|" + row[1],
                            row -> (row[2] != null) ? ((Number) row[2]).doubleValue() : 0.0
                    ));



            Map<String, Double> fullCumulativeMap = filteredCumulativeRaw.stream()
                    .collect(Collectors.toMap(
                            row -> row[0] + "|" + row[1],
                            row -> (row[2] != null) ? ((Number) row[2]).doubleValue() : 0.0
                    ));

            for (RemoteControlPlayedGraphDTO dto : rawStats) {
                String key = dto.getCountry() + "|" + dto.getCarName();
               for(int i=0;i<filteredCumulativeRaw.size();i++) {
                   if(filteredCumulativeRaw.get(i)[0].equals(dto.getCountry()) && filteredCumulativeRaw.get(i)[1].equals(dto.getCarName())) {
                       dto.setCumulativeRcDuration((Double) filteredCumulativeRaw.get(i)[2]);
                       dto.setCumulativeRacDurationInHours(String.valueOf(filteredCumulativeRaw.get(i)[5]));
                   }
               }
               dto.setTotalRacDurationInHours(String.valueOf(dto.getTotalRacDurationInHours()));
            }

            Map<String, List<RemoteControlPlayedGraphDTO>> graphGroupedByCountry = rawStats.stream()
                    .collect(Collectors.groupingBy(RemoteControlPlayedGraphDTO::getCountry));

            Map<String, Map<String, RemoteControlPlayedSummaryDTO>> summaryMap = new HashMap<>();

            for (RemoteControlPlayedGraphDTO dto : rawStats) {
                String country = dto.getCountry();
                String carName = dto.getCarName();
                double totalRc = dto.getTotalRcDuration();
                String totalRacDurationInHours = dto.getTotalRacDurationInHours();
                String cumulativeRacDurationInHours = dto.getCumulativeRacDurationInHours();
                String key = country + "|" + carName;

                double fullCumulative = fullCumulativeMap.getOrDefault(key, 0.0);

                summaryMap
                        .computeIfAbsent(country, k -> new HashMap<>())
                        .merge(carName,
                                new RemoteControlPlayedSummaryDTO(country, carName, totalRc, fullCumulative,totalRacDurationInHours,cumulativeRacDurationInHours),
                                (oldVal, newVal) -> {
                                    double totalRcPlayed = oldVal.getTotalRcDuration() + newVal.getTotalRcDuration();
                                    return new RemoteControlPlayedSummaryDTO(country, carName, totalRcPlayed, fullCumulative,totalRacDurationInHours,cumulativeRacDurationInHours);
                                });
            }

            List<RemoteControlPlayedSummaryDTO> summaryList = summaryMap.values().stream()
                    .flatMap(map -> map.values().stream())
                    .collect(Collectors.toList());

            double totalRcPlayed = summaryList.stream()
                    .mapToDouble(RemoteControlPlayedSummaryDTO::getTotalRcDuration)
                    .sum();

            BigDecimal totalCumulative = fullCumulativeMap.values().stream()
                    .map(BigDecimal::valueOf)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalRcPlayedBD = BigDecimal.valueOf(totalRcPlayed);

            Map<String, Object> responsePayload = new HashMap<>();
            responsePayload.put("graphData", graphGroupedByCountry);
            responsePayload.put("summary", summaryList);
            responsePayload.put("grandTotal", Map.of(
                    "rcPlayed", totalRcPlayedBD.toPlainString(),
                    "cumulative", totalCumulative.toPlainString()
            ));

            applicationResponse.setSuccess(true);
            applicationResponse.setData(responsePayload);

        } catch (Exception e) {
            applicationResponse.setSuccess(false);
            applicationResponse.setError("Error: " + e.getMessage());
        }

        return applicationResponse;
    }
}
