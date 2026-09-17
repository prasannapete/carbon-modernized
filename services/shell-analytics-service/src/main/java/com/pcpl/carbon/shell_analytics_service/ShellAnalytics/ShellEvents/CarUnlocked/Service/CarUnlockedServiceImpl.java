package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CarUnlocked.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CarUnlockedDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CarUnlockedGraphDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CarUnlockedSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.CarUnlocked;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CarUnlocked.Repository.CarUnlockedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service.UserInformationService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository.UserCountriesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CarUnlockedServiceImpl extends AbstractLazyService<CarUnlocked, CarUnlockedDTO, CarUnlockedRepository> implements CarUnlockedService {
    @Override
    public CarUnlocked getEntityObject() {
        return new CarUnlocked();
    }

    @Override
    public CarUnlockedDTO getDtoObject() {
        return new CarUnlockedDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getCarName";
    }

    @Autowired
    CarUnlockedRepository carUnlockedRepository;

    @Autowired
    UserInformationService userInformationService;

    @Autowired
    UserCountriesRepository userCountriesRepository;

    @Override
    public CarUnlockedDTO save(@RequestBody CarUnlockedDTO carUnlockedDTO) {
        return super.save(carUnlockedDTO);
    }

    @Override
    public ApplicationResponse getCarUnlockedData(Map<String, String> formData) throws Exception {
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
            List<CarUnlockedGraphDTO> rawStats = carUnlockedRepository.getCarUnlockStatsByDateRange(startDate, endDate,countryList);

            List<Object[]> cumulativeRaw = carUnlockedRepository.getTotalCumulativeCounts();
            Map<String, Long> cumulativeFullMap = cumulativeRaw.stream()
                    .collect(Collectors.toMap(
                            row -> row[0] + "|" + row[1],
                            row -> (Long) row[2]
                    ));

            for (CarUnlockedGraphDTO dto : rawStats) {
                String key = dto.getCountry() + "|" + dto.getCarName();
                dto.setCumulativeCount(cumulativeFullMap.getOrDefault(key, 0L));
            }

            Map<String, List<CarUnlockedGraphDTO>> graphGroupedByCountry = rawStats.stream()
                    .collect(Collectors.groupingBy(CarUnlockedGraphDTO::getCountry));

            Map<String, Map<String, CarUnlockedSummaryDTO>> summaryMap = new HashMap<>();
            for (CarUnlockedGraphDTO dto : rawStats) {
                String country = dto.getCountry();
                String carName = dto.getCarName();
                long cumulative = dto.getCumulativeCount();

                summaryMap
                        .computeIfAbsent(country, k -> new HashMap<>())
                        .merge(carName,
                                new CarUnlockedSummaryDTO(country, carName, dto.getUnlockedCount(), cumulative),
                                (oldVal, newVal) -> {
                                    long totalUnlocked = oldVal.getUnlocked() + newVal.getUnlocked();
                                    return new CarUnlockedSummaryDTO(country, carName, totalUnlocked, cumulative);
                                });
            }

            List<CarUnlockedSummaryDTO> summaryList = summaryMap.values().stream()
                    .flatMap(map -> map.values().stream())
                    .collect(Collectors.toList());

            // Step 7: Calculate grand totals
            long totalUnlocked = summaryList.stream().mapToLong(CarUnlockedSummaryDTO::getUnlocked).sum();
            long totalCumulative = cumulativeFullMap.values().stream()
                    .mapToLong(Long::longValue)
                    .sum();
            // Step 8: Package response
            Map<String, Object> responsePayload = new HashMap<>();
            responsePayload.put("graphData", graphGroupedByCountry);
            responsePayload.put("summary", summaryList);
            responsePayload.put("grandTotal", Map.of(
                    "unlocked", totalUnlocked,
                    "cumulative", totalCumulative
            ));

            applicationResponse.setSuccess(true);
            applicationResponse.setData(responsePayload);

        }catch (Exception e){
            applicationResponse.setSuccess(false);
            applicationResponse.setError(e.getMessage());
        }
        return applicationResponse;
    }

    @Override
    public ApplicationResponse getCarUnlockedDataByCountry(Map<String, String> formData) throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        try {
            String country = formData.get("country");
            List<CarUnlockedDTO> carUnlockeds = carUnlockedRepository.findAllByCountryAndIsDeletedDistinctOnCarName(country, 0);
            applicationResponse.setSuccess(true);
            applicationResponse.setData(carUnlockeds);
            applicationResponse.setMessage("Object fetched successfully!");
            applicationResponse.setError(null);
        }catch (Exception e){
            applicationResponse.setSuccess(false);
            applicationResponse.setError(e.getMessage());
        }
        return applicationResponse;
    }
    public ApplicationResponse getCountryWiseAverages(Map<String, String> formData) {
        User user = userInformationService.getUser();
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
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
            List<Object[]> data = null;
            if(countryList != null) {
                data = carUnlockedRepository.getCountryWiseUserAndEventCounts(startDate, endDate, countryList);
            }else{
                data = carUnlockedRepository.getUserAndEventCounts(startDate, endDate);
            }
            List<Map<String, Object>> result = new ArrayList<>();

            for (Object[] row : data) {
                Long countryId = (Long) row[0];
                String countryName = (String) row[1];
                String countryCode = (String) row[2];
                Long totalUsers = ((Number) row[3]).longValue();
                Long totalCarsUnlocked = ((Number) row[4]).longValue();
                Long totalRCPlayed = ((Number) row[5]).longValue();
                Long totalRacePlayed = ((Number) row[6]).longValue();
                Long cummulativeUsers = 0l;
                if(row[7]!=null) {
                     cummulativeUsers = ((Number) row[7]).longValue();
                }

                double avgCars = totalUsers > 0 ? (double) totalCarsUnlocked / totalUsers : 0;
                double avgRC = totalUsers > 0 ? (double) totalRCPlayed / totalUsers : 0;
                double avgRace = totalUsers > 0 ? (double) totalRacePlayed / totalUsers : 0;

                Map<String, Object> countryData = new HashMap<>();
                countryData.put("countryId", countryId);
                countryData.put("countryName", countryName);
                countryData.put("countryCode", countryCode);
                countryData.put("totalUsers", totalUsers);
                countryData.put("totalCarsUnlocked", totalCarsUnlocked);
                countryData.put("totalRCPlayed", totalRCPlayed);
                countryData.put("totalRacePlayed", totalRacePlayed);
                countryData.put("avgCars", avgCars);
                countryData.put("avgRC", avgRC);
                countryData.put("avgRace", avgRace);
                countryData.put("cummulativeUsers", cummulativeUsers);

                result.add(countryData);
            }
            applicationResponse.setSuccess(true);
            applicationResponse.setData(result);
        }
        catch (Exception e){
            applicationResponse.setSuccess(false);
            applicationResponse.setError(e.getMessage());
        }
        return applicationResponse;
    }
}
