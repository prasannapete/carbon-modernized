package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RacePlayed.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.RacePlayedDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.RacePlayedGraphDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.RemoteControlPlayedSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.RacePlayed;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RacePlayed.Repository.RacePlayedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service.UserInformationService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository.UserCountriesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RacePlayedServiceImpl extends AbstractLazyService<RacePlayed, RacePlayedDTO, RacePlayedRepository> implements RacePlayedService {
   @Autowired
   RacePlayedRepository racePlayedRepository;

   @Autowired
    UserInformationService userInformationService;

   @Autowired
    UserCountriesRepository userCountriesRepository;

    @Override
    public RacePlayed getEntityObject() {
        return new RacePlayed();
    }

    @Override
    public RacePlayedDTO getDtoObject() {
        return new RacePlayedDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getCarName";
    }

    @Override
    public RacePlayedDTO save(@RequestBody RacePlayedDTO challengeCompletedDTO) {
        return super.save(challengeCompletedDTO);
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
            List<RacePlayedGraphDTO> rawStats =
                    racePlayedRepository.getRemoteControlPlayedStatsByDateRange(startDate, endDate,countryList);

            List<Object[]> filteredCumulativeRaw =
                    racePlayedRepository.getTotalCumulativeCounts(countryList);

            Map<String, Double> filteredCumulativeMap = filteredCumulativeRaw.stream()
                    .collect(Collectors.toMap(
                            row -> row[0] + "|" + row[1],
                            row -> (row[2] != null) ? ((Number) row[2]).doubleValue() : 0.0
                    ));

            List<Object[]> fullCumulativeRaw =
                    racePlayedRepository.getTotalLifetimeCumulative();

            Map<String, Double> fullCumulativeMap = fullCumulativeRaw.stream()
                    .collect(Collectors.toMap(
                            row -> row[0] + "|" + row[1],
                            row -> (row[2] != null) ? ((Number) row[2]).doubleValue() : 0.0
                    ));

            for (RacePlayedGraphDTO dto : rawStats) {
                for(int i=0;i<filteredCumulativeRaw.size();i++) {
                    if(filteredCumulativeRaw.get(i)[0].equals(dto.getCountry()) && filteredCumulativeRaw.get(i)[1].equals(dto.getCarName())) {
                        dto.setCumulativeRcDuration((Double) filteredCumulativeRaw.get(i)[2]);
                        dto.setCumulativeRacDurationInHours(String.valueOf(filteredCumulativeRaw.get(i)[5]));
                    }
                }
                dto.setTotalRacDurationInHours(String.valueOf(dto.getTotalRacDurationInHours()));
            }

            Map<String, List<RacePlayedGraphDTO>> graphGroupedByCountry = rawStats.stream()
                    .collect(Collectors.groupingBy(RacePlayedGraphDTO::getCountry));

            Map<String, Map<String, RemoteControlPlayedSummaryDTO>> summaryMap = new HashMap<>();

            for (RacePlayedGraphDTO dto : rawStats) {
                String country = dto.getCountry();
                String carName = dto.getCarName();
                double totalRc = Math.round(new BigDecimal(dto.getTotalRcDuration())
                        .setScale(0, RoundingMode.HALF_UP).doubleValue());
                String totalRacDurationInHours = dto.getTotalRacDurationInHours();
                String cumulativeRacDurationInHours = dto.getCumulativeRacDurationInHours();
                String key = country + "|" + carName;
                double fullCumulative = Math.round(new BigDecimal(fullCumulativeMap.getOrDefault(key, 0.0))
                        .setScale(0, RoundingMode.HALF_UP).doubleValue());

                summaryMap
                        .computeIfAbsent(country, k -> new HashMap<>())
                        .merge(carName,
                                new RemoteControlPlayedSummaryDTO(country, carName, totalRc, fullCumulative,totalRacDurationInHours,cumulativeRacDurationInHours),
                                (oldVal, newVal) -> {
                                    double totalRcPlayed = new BigDecimal(oldVal.getTotalRcDuration() + newVal.getTotalRcDuration())
                                            .setScale(0, RoundingMode.HALF_UP).doubleValue();
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

    @Override
    public ApplicationResponse getCountryWiseCompetitionData(Map<String, String> formData) throws Exception {
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
            List<Object[]> rawStats =
                    racePlayedRepository.getCompetitionStatsByDateRange(startDate, endDate,countryList);

            Map<String, List<Map<String, Object>>> graphGroupedByCountry = new HashMap<>();

            for (Object[] row : rawStats) {
                String country = (String) row[0];
                graphGroupedByCountry
                        .computeIfAbsent(country, k -> new ArrayList<>())
                        .add(Map.of(
                                "country",row[0],
                                "countryName", row[1],
                                "total", row[2],
                                "unique", row[3]
                        ));
            }

            List<Map<String, Object>> summaryList = new ArrayList<>();

            for (Object[] row : rawStats) {
                Map<String, Object> summary = new HashMap<>();
                summary.put("country", row[0]);
                summary.put("countryName", row[1]);
                summary.put("total", row[2]);
                summary.put("unique", row[3]);
                summaryList.add(summary);
            }

            Map<String, Object> responsePayload = new HashMap<>();
            responsePayload.put("graphData", graphGroupedByCountry);
            responsePayload.put("summary", summaryList);

            applicationResponse.setSuccess(true);
            applicationResponse.setData(responsePayload);

        } catch (Exception e) {
            applicationResponse.setSuccess(false);
            applicationResponse.setError("Error: " + e.getMessage());
        }

        return applicationResponse;
    }
}
