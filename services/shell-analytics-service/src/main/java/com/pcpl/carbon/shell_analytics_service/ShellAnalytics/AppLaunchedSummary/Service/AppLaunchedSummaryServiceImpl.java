package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.AppLaunchedSummary.Service;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.AppLaunchedSummary.DTO.AppLaunchedSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.AppLaunchedSummary.Model.AppLaunchedSummary;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.AppLaunchedSummary.Repository.AppLaunchedSummaryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Repository.AppLaunchedRepository;
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
public class AppLaunchedSummaryServiceImpl extends AbstractLazyService<AppLaunchedSummary, AppLaunchedSummaryDTO, AppLaunchedSummaryRepository> implements AppLaunchedSummaryService {

    @Autowired
    AppLaunchedSummaryRepository appLaunchedSummaryRepository;

    @Autowired
    AppLaunchedRepository appLaunchedRepository;

    @Autowired
    UserInformationService userInformationService;

    @Autowired
    UserCountriesRepository userCountriesRepository;

    @Override
    public AppLaunchedSummary getEntityObject() {
        return new AppLaunchedSummary();
    }

    @Override
    public AppLaunchedSummaryDTO getDtoObject() {
        return new AppLaunchedSummaryDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }


    @Override
    public ApplicationResponse getSummaryByDate(String givenDate) {
        try {
            LocalDate date = LocalDate.parse(givenDate);
            List<Object[]> rows = appLaunchedSummaryRepository.getSummaryByDate(date);

            List<Map<String, Object>> result = new ArrayList<>();

            for (Object[] row : rows) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("country", row[0]);
                map.put("generatedDate", row[1]);
                map.put("appLaunchedCount", row[2]);
                map.put("cumulativeAppLaunchedCount", row[3]);
                result.add(map);
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
            Long todayCount = appLaunchedRepository.getCountByCountryAndDate(country, summaryDate);
            if (todayCount == null) {
                todayCount = 0L;
            }

            if (todayCount == 0) {
                log.info("No app launches for country {} on date {}, skipping summary generation", country, summaryDate);
                return;
            }

            Long cumulativeCount = appLaunchedRepository.getCumulativeCountByCountryUpToDate(country, summaryDate);
            if (cumulativeCount == null) {
                cumulativeCount = 0L;
            }


            List<AppLaunchedSummary> existingList = appLaunchedSummaryRepository.getSummaryByCountryAndDate(country, summaryDate);
            AppLaunchedSummary existing = existingList != null && !existingList.isEmpty() ? existingList.get(0) : null;

            if (existing != null) {
                existing.setGeneratedDate(summaryDate);
                existing.setAppLaunchedCount(todayCount);
                existing.setCumulativeAppLaunchedCount(cumulativeCount);
                existing.setLastModifiedTime(new java.util.Date());
                appLaunchedSummaryRepository.save(existing);
                log.info("Updated summary for country {} on date {}: count={}, cumulative={}",
                        country, summaryDate, todayCount, cumulativeCount);
            } else {
                AppLaunchedSummary summary = new AppLaunchedSummary();
                summary.setCountry(country);
                summary.setGeneratedDate(summaryDate);
                summary.setAppLaunchedCount(todayCount);
                summary.setCumulativeAppLaunchedCount(cumulativeCount);
                summary.setIsDeleted(0);
                summary.setCreationTime(new java.util.Date());
                appLaunchedSummaryRepository.save(summary);
                log.info("Created summary for country {} on date {}: count={}, cumulative={}",
                        country, summaryDate, todayCount, cumulativeCount);
            }

        } catch (Exception e) {
            log.error("Failed to generate EOD AppLaunchedSummary for country {} on {}",
                    country, summaryDate, e);
        }
    }


    @Override
    public Map<String, Object> getSummariesByUserCountries(String countries, String startDateStr, String endDateStr) throws Exception {
        User user = userInformationService.getUser();
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

        if (countryList.isEmpty()) {
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("countryWiseGraphData", new HashMap<>());
            responseMap.put("summaryTable", new ArrayList<>());
            responseMap.put("grandTotal", Map.of("launches", 0L, "cumulative", 0L));
            return responseMap;
        }

        List<Object[]> results = appLaunchedSummaryRepository.getSummariesByDateRange(startDate, endDate, countryList);

        List<Object[]> cumulativeRows = appLaunchedSummaryRepository.getLatestCumulativeCounts(countryList);

        Map<String, Long> cumulativeMap = cumulativeRows.stream()
                .collect(Collectors.toMap(
                        r -> r[0].toString(),
                        r -> ((Number) r[1]).longValue()
                ));

        Map<String, List<Map<String, Object>>> countryWiseGraphData = new HashMap<>();
        Map<String, Map<String, Object>> countrySummaryMap = new HashMap<>();

        for (Object[] row : results) {
            AppLaunchedSummary summary = (AppLaunchedSummary) row[0];
            String countryName = (String) row[1];

            String country = summary.getCountry();
            String displayCountryName = countryName != null ? countryName : country;

            Long countLong = summary.getAppLaunchedCount();

            if(countLong != null && countLong > 0) {
                Integer count = Math.toIntExact(countLong);

                countryWiseGraphData.computeIfAbsent(country, k -> new ArrayList<>()).add(
                        Map.of(
                                "date", formatter.format(summary.getGeneratedDate()),
                                "count", count
                        )
                );
            }

            if (!countrySummaryMap.containsKey(country)) {
                Map<String, Object> summaryData = new LinkedHashMap<>();
                summaryData.put("country", country);
                summaryData.put("launches", 0L);
                summaryData.put("cumulative", cumulativeMap.getOrDefault(country, 0L));
                summaryData.put("countryName", displayCountryName);
                countrySummaryMap.put(country, summaryData);
            }

            Map<String, Object> summaryData = countrySummaryMap.get(country);
            long currentLaunches = (Long) summaryData.get("launches");
            summaryData.put("launches", currentLaunches + summary.getAppLaunchedCount());

        }

        List<Map<String, Object>> summaryTable = new ArrayList<>(countrySummaryMap.values());

        long totalLaunches = 0;
        long totalCumulative = 0;
        for (Map<String, Object> summary : summaryTable) {
            totalLaunches += (Long) summary.get("launches");
            totalCumulative += (Long) summary.get("cumulative");
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("summaryTable", summaryTable);
        responseMap.put("grandTotal", Map.of("launches", totalLaunches, "cumulative", totalCumulative));
        responseMap.put("graphData", countryWiseGraphData);

        return responseMap;
    }

    @Override
    public void migrateAppLaunchData(String startDateStr, String endDateStr) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = formatter.parse(startDateStr);
        Date endDate = formatter.parse(endDateStr);

        log.info("Starting migration from {} to {}", startDateStr, endDateStr);

        Map<String, Long> runningCumulative = getStartingCumulatives(startDate);
        log.info("Initialized starting cumulative for {} countries", runningCumulative.size());

        List<Object[]> rows = appLaunchedRepository.getDailyCounts(startDate, endDate);
        log.info("Found {} records to migrate", rows.size());

        int processedCount = 0;
        int batchSize = 50;
        List<AppLaunchedSummary> batch = new ArrayList<>();

        String currentDate = null;

        for (Object[] row : rows) {
            String country = (String) row[0];
            Date generatedDate = (Date) row[1];
            Long dailyCount = ((Number) row[2]).longValue();

            String dateStr = formatter.format(generatedDate);

            if (!dateStr.equals(currentDate)) {
                if (currentDate != null) {
                    log.info("Completed processing date: {}", currentDate);
                }
                currentDate = dateStr;
                log.info("Processing date: {}", currentDate);
            }

            Long previousCumulative = runningCumulative.getOrDefault(country, 0L);
            Long newCumulative = previousCumulative + dailyCount;

            runningCumulative.put(country, newCumulative);

            log.debug("  └─ {}: daily={}, previous_cumulative={}, new_cumulative={}",
                    country, dailyCount, previousCumulative, newCumulative);

            List<AppLaunchedSummary> existingList =
                    appLaunchedSummaryRepository.findByCountryAndDate(country, generatedDate);

            AppLaunchedSummary summary;

            if (!existingList.isEmpty()) {
                summary = existingList.get(0);
                log.debug("      └─ Updating existing record");
            } else {
                summary = new AppLaunchedSummary();
                summary.setCountry(country);
                summary.setGeneratedDate(generatedDate);
                summary.setIsDeleted(0);
                summary.setCreationTime(new Date());
                log.debug("      └─ Creating new record");
            }

            summary.setAppLaunchedCount(dailyCount);
            summary.setCumulativeAppLaunchedCount(newCumulative);
            summary.setLastModifiedTime(new Date());

            batch.add(summary);
            processedCount++;

            if (batch.size() >= batchSize) {
                appLaunchedSummaryRepository.saveAll(batch);
                log.info("Saved batch of {} records. Total processed: {}", batch.size(), processedCount);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            appLaunchedSummaryRepository.saveAll(batch);
            log.info("Saved final batch of {} records", batch.size());
        }

        if (currentDate != null) {
            log.info("Completed processing date: {}", currentDate);
        }

        log.info("Migration complete: {} records processed from {} to {}",
                processedCount, startDateStr, endDateStr);
    }

    private Map<String, Long> getStartingCumulatives(Date startDate) {
        Map<String, Long> result = new HashMap<>();

        log.info("Fetching cumulative counts before {}", startDate);

        List<Object[]> priorCounts = appLaunchedRepository.getCumulativeCountsBeforeDate(startDate);

        for (Object[] row : priorCounts) {
            String country = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            result.put(country, count);
            log.info("  → {}: starting cumulative = {}", country, count);
        }

        if (priorCounts.isEmpty()) {
            log.info("No prior data found. Starting cumulative from 0 for all countries.");
        }

        return result;
    }
}
