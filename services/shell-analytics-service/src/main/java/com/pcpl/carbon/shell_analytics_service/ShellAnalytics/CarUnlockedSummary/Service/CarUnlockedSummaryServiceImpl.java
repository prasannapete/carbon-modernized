package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.CarUnlockedSummary.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.CarUnlockedSummary.DTO.CarUnlockedSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.CarUnlockedSummary.Model.CarUnlockedSummary;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.CarUnlockedSummary.Repository.CarUnlockedSummaryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CarUnlocked.Repository.CarUnlockedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service.UserInformationService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository.UserCountriesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CarUnlockedSummaryServiceImpl extends AbstractLazyService<CarUnlockedSummary, CarUnlockedSummaryDTO, CarUnlockedSummaryRepository> implements CarUnlockedSummaryService {

    @Autowired
    CarUnlockedSummaryRepository carUnlockedSummaryRepository;

    @Autowired
    CarUnlockedRepository carUnlockedRepository;

    @Autowired
    UserInformationService userInformationService;

    @Autowired
    UserCountriesRepository userCountriesRepository;

    @Override
    public CarUnlockedSummary getEntityObject() {
        return new CarUnlockedSummary();
    }

    @Override
    public CarUnlockedSummaryDTO getDtoObject() {
        return new CarUnlockedSummaryDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "carName";
    }

    @Override
    public ApplicationResponse getSummaryByDate(String givenDate) {
        try {
            LocalDate date = LocalDate.parse(givenDate);
            List<Object[]> rows = carUnlockedSummaryRepository.getSummaryByDate(date);

            Map<String, Date> countryDateMap = new LinkedHashMap<>();
            Map<String, List<Map<String, Object>>> countryCarMap = new LinkedHashMap<>();

            for (Object[] row : rows) {
                String country = (String) row[0];
                String carName = (String) row[1];
                Date generatedDate = (Date) row[2];
                Long unlockedCount = (Long) row[3];
                Long cumulativeCount = (Long) row[4];

                countryDateMap.putIfAbsent(country, generatedDate);
                countryCarMap.putIfAbsent(country, new ArrayList<>());

                Map<String, Object> carData = new LinkedHashMap<>();
                carData.put("carName", carName);
                carData.put("unlockedCount", unlockedCount);
                carData.put("cumulativeUnlockedCount", cumulativeCount);

                countryCarMap.get(country).add(carData);
            }

            List<Map<String, Object>> result = new ArrayList<>();

            for (Map.Entry<String, List<Map<String, Object>>> entry : countryCarMap.entrySet()) {
                Map<String, Object> countryData = new LinkedHashMap<>();
                countryData.put("country", entry.getKey());
                countryData.put("generatedDate", countryDateMap.get(entry.getKey()));
                countryData.put("cars", entry.getValue());
                result.add(countryData);
            }

            return ApplicationResponse.builder()
                    .data(result)
                    .success(true)
                    .code(200)
                    .message("Summary fetched successfully for date: " + givenDate)
                    .error(null)
                    .totalPages(0)
                    .recordsTotal(null)
                    .currentRecords(result.size())
                    .recordsFiltered(null)
                    .build();

        } catch (Exception e) {
            return ApplicationResponse.builder()
                    .data(null)
                    .success(false)
                    .code(500)
                    .message("Failed to fetch summary")
                    .error(e.getMessage())
                    .totalPages(0)
                    .recordsTotal(null)
                    .currentRecords(0)
                    .recordsFiltered(null)
                    .build();
        }
    }

    @Override
    public void generateEndOfDaySummaryForCountry(String country, Date summaryDate) {
        try {

            List<String> carNames = carUnlockedRepository.getCarNamesByCountryAndDate(country, summaryDate);
            if (carNames == null || carNames.isEmpty()) {
                log.info("No unlocked car records found for country {} on {}, skipping summary", country, summaryDate);
                return;
            }

            for (String carName : carNames) {

                Long todayUnlocked = carUnlockedRepository.getCountByCountryCarAndDate(country, carName, summaryDate);
                if (todayUnlocked == null) todayUnlocked = 0L;

                if (todayUnlocked <= 0) {
                    log.info("Zero unlocked count for country={} car={} on {}, skipping",
                            country, carName, summaryDate);
                    continue;
                }

                Long cumulativeUnlocked = carUnlockedRepository.getCumulativeCountByCountryCarUpToDate(country, carName, summaryDate);
                if (cumulativeUnlocked == null) cumulativeUnlocked = 0L;

                List<CarUnlockedSummary> existingList =
                        carUnlockedSummaryRepository.getSummaryByCountryCarAndDate(country, carName, summaryDate);

                CarUnlockedSummary existing = (existingList != null && !existingList.isEmpty())
                        ? existingList.get(0) : null;

                if (existing != null) {
                    existing.setGeneratedDate(summaryDate);
                    existing.setUnlockedCount(todayUnlocked);
                    existing.setCumulativeUnlockedCount(cumulativeUnlocked);
                    existing.setLastModifiedTime(new java.util.Date());
                    carUnlockedSummaryRepository.save(existing);

                    log.info("UPDATED CarUnlockedSummary country={} car={} today={} cumulative={}",
                            country, carName, todayUnlocked, cumulativeUnlocked);

                } else {
                    CarUnlockedSummary summary = new CarUnlockedSummary();
                    summary.setCountry(country);
                    summary.setCarName(carName);
                    summary.setGeneratedDate(summaryDate);
                    summary.setUnlockedCount(todayUnlocked);
                    summary.setCumulativeUnlockedCount(cumulativeUnlocked);
                    summary.setIsDeleted(0);
                    summary.setCreationTime(new java.util.Date());
                    carUnlockedSummaryRepository.save(summary);

                    log.info("INSERTED CarUnlockedSummary country={} car={} today={} cumulative={}",
                            country, carName, todayUnlocked, cumulativeUnlocked);
                }
            }

        } catch (Exception e) {
            log.error("Failed to generate EOD CarUnlockedSummary for country {} on {}",
                    country, summaryDate, e);
        }
    }


    @Override
    public Map<String, Object> getSummaryByDateRange(String startDateStr, String endDateStr, String country) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = formatter.parse(startDateStr);
        Date endDate   = formatter.parse(endDateStr);

        User user = userInformationService.getUser();

        List<String> requestedCountries = new ArrayList<>();

        if (country != null && !country.trim().isEmpty()) {
            requestedCountries = Arrays.stream(country.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        Set<String> allowedCountries = userCountriesRepository.getUserCountriesByUserId(user.getId())
                .stream()
                .map(UserCountriesDTO::getCountryCode)
                .collect(Collectors.toSet());

        List<String> finalCountries;

        if (!requestedCountries.isEmpty()) {
            finalCountries = requestedCountries.stream()
                    .filter(allowedCountries::contains)
                    .toList();

            if (finalCountries.isEmpty()) {
                return Map.of(
                        "summary", List.of(),
                        "grandTotal", Map.of("unlocked", 0, "cumulative", 0),
                        "graphData", Map.of()
                );
            }
        } else {
            finalCountries = new ArrayList<>(allowedCountries);
        }

        List<Object[]> rows = carUnlockedSummaryRepository.getUnlockedCountsPerCar(startDate, endDate, finalCountries);

        List<Object[]> cumulativeRows = carUnlockedSummaryRepository.getLatestCumulativeCounts(finalCountries);

        Map<String, Long> cumulativeMap = cumulativeRows.stream()
                .collect(Collectors.toMap(
                        row -> row[0] + "|" + row[1],   // key = country|carName
                        row -> Long.parseLong(row[2].toString())
                ));

        List<Map<String, Object>> summaryList = new ArrayList<>();
        Map<String, List<Map<String, Object>>> graphData = new LinkedHashMap<>();

        long totalUnlocked = 0;
        long totalCumulative = 0;

        for (Object[] row : rows) {

            String countryCode  = (String) row[0];
            String carName      = (String) row[1];
            String countryName  = (String) row[2];
            Integer isActive    = ((Number) row[3]).intValue();
            Long unlocked       = ((Number) row[4]).longValue();

            String key = countryCode + "|" + carName;
            Long cumulative = cumulativeMap.getOrDefault(key, 0L);

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("country", countryCode);
            summary.put("carName", carName);
            summary.put("unlocked", unlocked);
            summary.put("cumulative", cumulative);

            summaryList.add(summary);

            totalUnlocked += unlocked;
            totalCumulative += cumulative;

            Map<String, Object> graphPoint = new LinkedHashMap<>();
            graphPoint.put("country", countryCode);
            graphPoint.put("carName", carName);
            graphPoint.put("countryName", countryName != null ? countryName : countryCode);
            graphPoint.put("isActive", isActive);
            graphPoint.put("unlockedCount", unlocked);
            graphPoint.put("cumulativeCount", cumulative);

            graphData.computeIfAbsent(countryCode, k -> new ArrayList<>()).add(graphPoint);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("summaryTable", summaryList);
        response.put("grandTotal", Map.of(
                "unlocked", totalUnlocked,
                "cumulative", totalCumulative
        ));
        response.put("graphData", graphData);

        return response;
    }

    @Override
    public void migrateCarUnlockedData(String startDateStr, String endDateStr) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = formatter.parse(startDateStr);
        Date endDate = formatter.parse(endDateStr);

        log.info("Starting Car Unlocked migration from {} to {}", startDateStr, endDateStr);

        Map<String, Long> runningCumulative = getStartingCumulatives(startDate);
        log.info("Initialized starting cumulative for {} country-car combinations", runningCumulative.size());

        List<Object[]> rows = carUnlockedRepository.getDailyCounts(startDate, endDate);
        log.info("Found {} records to migrate", rows.size());

        int processedCount = 0;
        int batchSize = 50;
        List<CarUnlockedSummary> batch = new ArrayList<>();

        String currentDate = null;

        for (Object[] row : rows) {
            String country = (String) row[0];
            String carName = (String) row[1];
            Date generatedDate = (Date) row[2];
            Long dailyCount = ((Number) row[3]).longValue();

            String dateStr = formatter.format(generatedDate);

            if (!dateStr.equals(currentDate)) {
                if (currentDate != null) {
                    log.info("Completed processing date: {}", currentDate);
                }
                currentDate = dateStr;
                log.info("Processing date: {}", currentDate);
            }

            String key = country + "|" + carName;

            Long previousCumulative = runningCumulative.getOrDefault(key, 0L);
            Long newCumulative = previousCumulative + dailyCount;

            runningCumulative.put(key, newCumulative);

            log.debug("  └─ {} - {}: daily={}, previous_cumulative={}, new_cumulative={}",
                    country, carName, dailyCount, previousCumulative, newCumulative);

            List<CarUnlockedSummary> existingList =
                    carUnlockedSummaryRepository.findByCountryCarNameAndDate(country, carName, generatedDate);

            CarUnlockedSummary summary;

            if (!existingList.isEmpty()) {
                summary = existingList.get(0);
                log.debug("      └─ Updating existing record");
            } else {
                summary = new CarUnlockedSummary();
                summary.setCountry(country);
                summary.setCarName(carName);
                summary.setGeneratedDate(generatedDate);
                summary.setIsDeleted(0);
                summary.setCreationTime(new Date());
                log.debug("      └─ Creating new record");
            }

            summary.setUnlockedCount(dailyCount);
            summary.setCumulativeUnlockedCount(newCumulative);
            summary.setLastModifiedTime(new Date());

            batch.add(summary);
            processedCount++;

            if (batch.size() >= batchSize) {
                carUnlockedSummaryRepository.saveAll(batch);
                log.info("Saved batch of {} records. Total processed: {}", batch.size(), processedCount);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            carUnlockedSummaryRepository.saveAll(batch);
            log.info("Saved final batch of {} records", batch.size());
        }

        if (currentDate != null) {
            log.info("Completed processing date: {}", currentDate);
        }

        log.info("Car Unlocked migration complete: {} records processed from {} to {}",
                processedCount, startDateStr, endDateStr);
    }

    private Map<String, Long> getStartingCumulatives(Date startDate) {
        Map<String, Long> result = new HashMap<>();

        log.info("Fetching car unlocked cumulative counts before {}", startDate);

        List<Object[]> priorCounts = carUnlockedRepository.getCumulativeCountsBeforeDate(startDate);

        for (Object[] row : priorCounts) {
            String country = (String) row[0];
            String carName = (String) row[1];
            Long count = ((Number) row[2]).longValue();

            String key = country + "|" + carName;
            result.put(key, count);
            log.info("  → {} - {}: starting cumulative = {}", country, carName, count);
        }

        if (priorCounts.isEmpty()) {
            log.info("No prior car unlocked data found. Starting cumulative from 0 for all combinations.");
        }

        return result;
    }
}
