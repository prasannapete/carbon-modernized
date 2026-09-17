package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RCPlayedSummary.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.RCPlayedSummary.DTO.RCPlayedSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.RCPlayedSummary.Model.RCPlayedSummary;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RCPlayedSummary.Repository.RCPlayedSummaryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RCPlayed.Repository.RCPlayedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service.UserInformationService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository.UserCountriesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RCPlayedSummaryServiceImpl extends AbstractLazyService<RCPlayedSummary, RCPlayedSummaryDTO, RCPlayedSummaryRepository> implements RCPlayedSummaryService {

    @Autowired
    RCPlayedSummaryRepository rcPlayedSummaryRepository;

    @Autowired
    RCPlayedRepository rcPlayedRepository;

    @Autowired
    UserInformationService userInformationService;

    @Autowired
    UserCountriesRepository userCountriesRepository;

    @Override
    public RCPlayedSummary getEntityObject() {
        return new RCPlayedSummary();
    }

    @Override
    public RCPlayedSummaryDTO getDtoObject() {
        return new RCPlayedSummaryDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }

    @Override
    public RCPlayedSummaryDTO save(@RequestBody RCPlayedSummaryDTO challengeCompletedDTO) {
        return super.save(challengeCompletedDTO);
    }

    @Override
    public ApplicationResponse getSummaryByDate(String givenDate) {
        try {
            LocalDate date = LocalDate.parse(givenDate);
            List<Object[]> rows = rcPlayedSummaryRepository.getSummaryByDate(date);

            Map<String, Date> countryDateMap = new LinkedHashMap<>();
            Map<String, List<Map<String, Object>>> countryCarMap = new LinkedHashMap<>();

            for (Object[] row : rows) {
                String country = (String) row[0];
                String carName = (String) row[1];
                Date generatedDate = (Date) row[2];
                Object rcDurationObj = row[3];
                String cumulativeDuration = (String) row[4];

                Float rcDuration = rcDurationObj instanceof Double
                        ? ((Double) rcDurationObj).floatValue()
                        : (Float) rcDurationObj;

                countryDateMap.putIfAbsent(country, generatedDate);
                countryCarMap.putIfAbsent(country, new ArrayList<>());

                Map<String, Object> carData = new LinkedHashMap<>();
                carData.put("carName", carName);
                carData.put("rcDuration", rcDuration);
                carData.put("cumulativeRCDuration", cumulativeDuration);

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


            List<String> carNames = rcPlayedRepository.getCarNamesByCountryAndDate(country, summaryDate);

            if (carNames == null || carNames.isEmpty()) {
                log.info("No RC Played data found for country {} on {}, skipping", country, summaryDate);
                return;
            }

            for (String carName : carNames) {

                Float todayDuration = rcPlayedRepository.getDurationByCountryCarAndDate(country, carName, summaryDate);
                if (todayDuration == null) todayDuration = 0.0f;

                if (todayDuration <= 0) {
                    log.info("Zero RC duration for country={} car={} on {}, skipping",
                            country, carName, summaryDate);
                    continue;
                }

                Float cumulativeDuration = rcPlayedRepository.getCumulativeDurationByCountryCarUpToDate(country, carName, summaryDate);
                if (cumulativeDuration == null) cumulativeDuration = 0.0f;


                List<RCPlayedSummary> existingList =
                        rcPlayedSummaryRepository.getSummaryByCountryCarAndDate(country, carName, summaryDate);

                RCPlayedSummary existing = (existingList != null && !existingList.isEmpty())
                        ? existingList.get(0) : null;

                if (existing != null) {
                    existing.setGeneratedDate(summaryDate);
                    existing.setRcDuration(todayDuration);
                    existing.setCumulativeRCDuration(String.valueOf(cumulativeDuration));
                    existing.setLastModifiedTime(new java.util.Date());
                    rcPlayedSummaryRepository.save(existing);

                    log.info("UPDATED RCPlayedSummary country={} car={} today={} cumulative={}",
                            country, carName, todayDuration, cumulativeDuration);

                } else {
                    RCPlayedSummary summary = new RCPlayedSummary();
                    summary.setCountry(country);
                    summary.setCarName(carName);
                    summary.setGeneratedDate(summaryDate);
                    summary.setRcDuration(todayDuration);
                    summary.setCumulativeRCDuration(String.valueOf(cumulativeDuration));
                    summary.setIsDeleted(0);
                    summary.setCreationTime(new java.util.Date());
                    rcPlayedSummaryRepository.save(summary);

                    log.info("CREATED RCPlayedSummary country={} car={} today={} cumulative={}",
                            country, carName, todayDuration, cumulativeDuration);
                }
            }

        } catch (Exception e) {
            log.error("Failed to generate EOD RCPlayedSummary for country {} on {}",
                    country, summaryDate, e);
        }
    }


    @Override
    public Map<String, Object> getSummaryByDateRange(String startDateStr, String endDateStr, String country) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date from = formatter.parse(startDateStr);
        Date to = formatter.parse(endDateStr);

        User user = userInformationService.getUser();

        List<String> requestedCountries = new ArrayList<>();
        if (country != null && !country.trim().isEmpty()) {
            requestedCountries = Arrays.stream(country.split(","))
                    .map(String::trim)
                    .toList();
        }

        Set<String> allowedCountries = userCountriesRepository.getUserCountriesByUserId(user.getId())
                .stream()
                .map(UserCountriesDTO::getCountryCode)
                .collect(Collectors.toSet());

        List<String> finalCountries = requestedCountries.isEmpty() ?
                new ArrayList<>(allowedCountries) :
                requestedCountries.stream().filter(allowedCountries::contains).toList();

        if (finalCountries.isEmpty()) {
            return Map.of(
                    "summary", List.of(),
                    "graphData", Map.of(),
                    "grandTotal", Map.of("rcPlayed", "0", "cumulative", "0")
            );
        }

        List<Object[]> durationRows =
                rcPlayedSummaryRepository.getSummariesByDateRange(from, to, finalCountries);

        List<Object[]> cumulativeRows =
                rcPlayedSummaryRepository.getLatestCumulativeDurations(finalCountries);

        Map<String, Float> cumulativeMap = new HashMap<>();
        for (Object[] row : cumulativeRows) {
            cumulativeMap.put(
                    row[0] + "|" + row[1],
                    toFloat(row[2])
            );
        }

        List<Map<String, Object>> summaryList = new ArrayList<>();
        Map<String, List<Map<String, Object>>> graphData = new LinkedHashMap<>();

        float totalPlayedMinutes = 0;
        float totalCumulativeMinutes = 0;

        for (Object[] row : durationRows) {

            String countryCode = (String) row[0];
            String carName = (String) row[1];
            String countryName = (String) row[2];
            Integer isActive = ((Number) row[3]).intValue();
            Float durationInSeconds = ((Number) row[4]).floatValue();

            Float durationInMinutes = durationInSeconds / 60;
            Float cumulativeMinutes = cumulativeMap.getOrDefault(countryCode + "|" + carName, 0f) / 60;

            String formattedDuration = convertMinutes(durationInMinutes);
            String formattedCumulative = convertMinutes(cumulativeMinutes);

            totalPlayedMinutes += durationInMinutes;
            totalCumulativeMinutes += cumulativeMinutes;

            Map<String, Object> responseRow = new LinkedHashMap<>();
            responseRow.put("country", countryCode);
            responseRow.put("carName", carName);
            responseRow.put("totalRcDuration", durationInMinutes);
            responseRow.put("cumulativeRcDuration", cumulativeMinutes);
            responseRow.put("totalRcDurationInHours", formattedDuration);
            responseRow.put("cumulativeRcDurationInHours", formattedCumulative);

            summaryList.add(responseRow);

            Map<String, Object> graphRow = new LinkedHashMap<>(responseRow);
            graphRow.put("countryName", countryName != null ? countryName : countryCode);
            graphRow.put("isActive", isActive);

            graphData.computeIfAbsent(countryCode, k -> new ArrayList<>()).add(graphRow);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("summaryTable", summaryList);
        response.put("grandTotal", Map.of(
                "racePlayed", convertMinutes(totalPlayedMinutes),
                "cumulative", convertMinutes(totalCumulativeMinutes)
        ));
        response.put("graphData", graphData);

        return response;
    }
    private Float toFloat(Object v) {
        try {
            if (v == null) return 0f;
            if (v instanceof Number) return ((Number) v).floatValue();
            return Float.parseFloat(v.toString());
        } catch (Exception e) {
            return 0f;
        }
    }

    private static String convertMinutes(Float minutes) {
        if (minutes == null || minutes <= 0) return "0 h 0 min";

        long totalMinutes = Math.round(minutes);
        long hours = totalMinutes / 60;
        long remainingMinutes = totalMinutes % 60;

        return hours + " h " + remainingMinutes + " min";
    }

    @Override
    public void migrateRCPlayedData(String startDateStr, String endDateStr) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = formatter.parse(startDateStr);
        Date endDate = formatter.parse(endDateStr);

        log.info("Starting RC Played migration from {} to {}", startDateStr, endDateStr);

        Map<String, Double> runningCumulative = getStartingCumulatives(startDate);

        List<Object[]> rows = rcPlayedRepository.getDailyRCDuration(startDate, endDate);
        log.info("Found {} records to migrate", rows.size());

        List<RCPlayedSummary> batch = new ArrayList<>();
        int batchSize = 50;
        int processed = 0;

        String currentDate = null;

        for (Object[] row : rows) {
            String country = (String) row[0];
            String carName = (String) row[1];
            Date generatedDate = (Date) row[2];
            Double dailyDuration = ((Number) row[3]).doubleValue();

            String dateStr = formatter.format(generatedDate);
            if (!dateStr.equals(currentDate)) {
                if (currentDate != null) {
                    log.info("Completed processing date: {}", currentDate);
                }
                currentDate = dateStr;
                log.info("Processing date: {}", currentDate);
            }

            String key = country + "|" + carName;

            Double previous = runningCumulative.getOrDefault(key, 0.0);
            Double newCumulative = previous + dailyDuration;
            runningCumulative.put(key, newCumulative);

            List<RCPlayedSummary> existing =
                    rcPlayedSummaryRepository.findByCountryCarAndDate(country, carName, generatedDate);

            RCPlayedSummary summary;
            if (!existing.isEmpty()) {
                summary = existing.get(0);
            } else {
                summary = new RCPlayedSummary();
                summary.setCountry(country);
                summary.setCarName(carName);
                summary.setGeneratedDate(generatedDate);
                summary.setIsDeleted(0);
                summary.setCreationTime(new Date());
            }

            summary.setRcDuration(dailyDuration.floatValue());
            summary.setCumulativeRCDuration(String.format("%.2f", newCumulative));
            summary.setLastModifiedTime(new Date());

            batch.add(summary);
            processed++;

            if (batch.size() >= batchSize) {
                rcPlayedSummaryRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            rcPlayedSummaryRepository.saveAll(batch);
        }

        log.info("RC Played migration completed: {} records processed", processed);
    }

    private Map<String, Double> getStartingCumulatives(Date startDate) {

        Map<String, Double> map = new HashMap<>();

        log.info("Fetching RC cumulative duration before {}", startDate);

        List<Object[]> rows =
                rcPlayedRepository.getCumulativeRCDurationBeforeDate(startDate);

        for (Object[] row : rows) {
            String country = (String) row[0];
            String car = (String) row[1];
            Double duration = ((Number) row[2]).doubleValue();

            map.put(country + "|" + car, duration);
            log.info("  → {} - {} : starting cumulative = {}", country, car, duration);
        }

        return map;
    }
}
