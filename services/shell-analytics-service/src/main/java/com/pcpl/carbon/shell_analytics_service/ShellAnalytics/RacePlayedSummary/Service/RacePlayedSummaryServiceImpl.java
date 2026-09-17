package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RacePlayedSummary.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.RacePlayedSummary.Model.RacePlayedSummary;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.RacePlayedSummary.DTO.RacePlayedSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RacePlayedSummary.Repository.RacePlayedSummaryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RacePlayed.Repository.RacePlayedRepository;
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
public class RacePlayedSummaryServiceImpl extends AbstractLazyService<RacePlayedSummary, RacePlayedSummaryDTO, RacePlayedSummaryRepository> implements RacePlayedSummaryService {
    @Autowired
    RacePlayedSummaryRepository racePlayedSummaryRepository;

    @Autowired
    RacePlayedRepository  racePlayedRepository;

    @Autowired
    UserInformationService userInformationService;

    @Autowired
    UserCountriesRepository userCountriesRepository;
    
    @Override
    public RacePlayedSummary getEntityObject() {
        return new RacePlayedSummary();
    }

    @Override
    public RacePlayedSummaryDTO getDtoObject() {
        return new RacePlayedSummaryDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }

//    @Override
//// public RacePlayedSummaryDTO save(RacePlayedSummaryDTO challengeCompletedDTO) {
//// return super.save(challengeCompletedDTO);
//// }
    @Override
    public RacePlayedSummaryDTO save(RacePlayedSummaryDTO dto) {
    return super.save(dto);
}

    @Override
    public ApplicationResponse getSummaryByDate(String givenDate) {
        try {
            LocalDate date = LocalDate.parse(givenDate);
            List<Object[]> rows = racePlayedSummaryRepository.getSummaryByDate(date);

            Map<String, Date> countryDateMap = new LinkedHashMap<>();
            Map<String, List<Map<String, Object>>> countryCarMap = new LinkedHashMap<>();

            for (Object[] row : rows) {
                String country = (String) row[0];
                String carName = (String) row[1];
                Date generatedDate = (Date) row[2];
                Object raceDurationObj = row[3];
                String cumulativeDuration = (String) row[4];

                Float raceDuration = raceDurationObj instanceof Double
                        ? ((Double) raceDurationObj).floatValue()
                        : (Float) raceDurationObj;

                countryDateMap.putIfAbsent(country, generatedDate);
                countryCarMap.putIfAbsent(country, new ArrayList<>());

                Map<String, Object> carData = new LinkedHashMap<>();
                carData.put("carName", carName);
                carData.put("raceDuration", raceDuration);
                carData.put("cumulativeRaceDuration", cumulativeDuration);

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

            List<String> carNames = racePlayedRepository.getCarNamesByCountryAndDate(country, summaryDate);
            if (carNames == null || carNames.isEmpty()) {
                log.info("No race played for country {} on {}, skipping.", country, summaryDate);
                return;
            }

            for (String carName : carNames) {

                Float todayDuration = racePlayedRepository.getDurationByCountryCarAndDate(country, carName, summaryDate);
                if (todayDuration == null) todayDuration = 0.0f;

                if (todayDuration <= 0) {
                    log.info("Zero duration for country={}, car={}, date={} → skipping record",
                            country, carName, summaryDate);
                    continue;
                }

                Float cumulativeDuration = racePlayedRepository.getCumulativeDurationByCountryCarUpToDate(country, carName, summaryDate);
                if (cumulativeDuration == null) cumulativeDuration = 0.0f;

                List<RacePlayedSummary> existingList =
                        racePlayedSummaryRepository.getSummaryByCountryCarAndDate(country, carName, summaryDate);

                RacePlayedSummary existing =
                        (existingList != null && !existingList.isEmpty()) ? existingList.get(0) : null;

                if (existing != null) {
                    existing.setGeneratedDate(summaryDate);
                    existing.setRaceDuration(todayDuration);
                    existing.setCumulativeRaceDuration(String.valueOf(cumulativeDuration));
                    existing.setLastModifiedTime(new java.util.Date());
                    racePlayedSummaryRepository.save(existing);

                    log.info("UPDATED: country={}, car={}, today={}, cumulative={}",
                            country, carName, todayDuration, cumulativeDuration);

                } else {
                    RacePlayedSummary summary = new RacePlayedSummary();
                    summary.setCountry(country);
                    summary.setCarName(carName);
                    summary.setGeneratedDate(summaryDate);
                    summary.setRaceDuration(todayDuration);
                    summary.setCumulativeRaceDuration(String.valueOf(cumulativeDuration));
                    summary.setIsDeleted(0);
                    summary.setCreationTime(new java.util.Date());
                    racePlayedSummaryRepository.save(summary);

                    log.info("INSERTED: country={}, car={}, today={}, cumulative={}",
                            country, carName, todayDuration, cumulativeDuration);
                }
            }

        } catch (Exception e) {
            log.error("Failed to generate EOD RacePlayedSummary for country {} on {}",
                    country, summaryDate, e);
        }
    }


    @Override
    public Map<String, Object> getSummaryByDateRange(String startDate, String endDate, String country) throws Exception {

        User user = userInformationService.getUser();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date from = formatter.parse(startDate);
        Date to = formatter.parse(endDate);

        Set<String> allowedCountries = userCountriesRepository.getUserCountriesByUserId(user.getId())
                .stream()
                .map(UserCountriesDTO::getCountryCode)
                .collect(Collectors.toSet());

        List<String> requestedCountries = new ArrayList<>();

        if (country != null && !country.trim().isEmpty()) {
            requestedCountries = Arrays.stream(country.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        List<String> finalCountries;
        if (!requestedCountries.isEmpty()) {
            finalCountries = requestedCountries.stream()
                    .filter(allowedCountries::contains)
                    .toList();

            if (finalCountries.isEmpty()) {
                return Map.of(
                        "summary", List.of(),
                        "graphData", Map.of(),
                        "grandTotal", Map.of("racePlayed", "0 h 0 min", "cumulative", "0 h 0 min")
                );
            }
        } else {
            finalCountries = new ArrayList<>(allowedCountries);
        }

        List<Object[]> durationRows =
                racePlayedSummaryRepository.getSummariesByDateRange(from, to, finalCountries);

        List<Object[]> cumulativeRows =
                racePlayedSummaryRepository.getLatestCumulativeDurations(finalCountries);

        Map<String, Float> cumulativeMap = new HashMap<>();
        for (Object[] row : cumulativeRows) {
            String key = row[0] + "|" + row[1];
            cumulativeMap.put(key, toFloat(row[2]));
        }

        List<Map<String, Object>> summaryList = new ArrayList<>();
        Map<String, List<Map<String, Object>>> graphData = new LinkedHashMap<>();

        float totalPlayedMinutes = 0;
        float totalCumulativeMinutes = 0;

        for (Object[] row : durationRows) {

            String countryCode = (String) row[0];
            String carName = (String) row[1];
            String countryName = (String) row[2];
            Integer isActive = (row[3] != null) ? ((Number) row[3]).intValue() : 0;
            Float durationSeconds = (row[4] != null) ? ((Number) row[4]).floatValue() : 0f;

            Float durationMinutes = durationSeconds / 60f;

            String key = countryCode + "|" + carName;
            Float cumulativeMinutes = cumulativeMap.getOrDefault(key, 0f) / 60f;

            String formattedDuration = convertMinutes(durationMinutes);
            String formattedCumulative = convertMinutes(cumulativeMinutes);

            Map<String, Object> summaryItem = new LinkedHashMap<>();
            summaryItem.put("country", countryCode);
            summaryItem.put("carName", carName);
            summaryItem.put("totalRaceDuration", durationMinutes);
            summaryItem.put("cumulativeRaceDuration", cumulativeMinutes);
            summaryItem.put("totalRaceDurationInHours", formattedDuration);
            summaryItem.put("cumulativeRaceDurationInHours", formattedCumulative);

            summaryList.add(summaryItem);

            totalPlayedMinutes += durationMinutes;
            totalCumulativeMinutes += cumulativeMinutes;

            Map<String, Object> graphRow = new LinkedHashMap<>();
            graphRow.put("country", countryCode);
            graphRow.put("carName", carName);
            graphRow.put("totalRaceDuration", durationMinutes);
            graphRow.put("cumulativeRaceDuration", cumulativeMinutes);
            graphRow.put("countryName", countryName != null ? countryName : countryCode);
            graphRow.put("isActive", isActive);
            graphRow.put("totalRaceDurationInHours", formattedDuration);
            graphRow.put("cumulativeRaceDurationInHours", formattedCumulative);

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
            if (v instanceof Number) return ((Number)v).floatValue();
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
    public void migrateRacePlayedData(String startDateStr, String endDateStr) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = formatter.parse(startDateStr);
        Date endDate = formatter.parse(endDateStr);

        log.info("Starting Race Played migration from {} to {}", startDateStr, endDateStr);

        Map<String, Double> runningCumulative = getStartingCumulatives(startDate);

        List<Object[]> rows = racePlayedRepository.getDailyRaceDuration(startDate, endDate);
        log.info("Found {} records to migrate", rows.size());

        List<RacePlayedSummary> batch = new ArrayList<>();
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

            List<RacePlayedSummary> existing =
                    racePlayedSummaryRepository.findByCountryCarAndDate(country, carName, generatedDate);

            RacePlayedSummary summary;
            if (!existing.isEmpty()) {
                summary = existing.get(0);
            } else {
                summary = new RacePlayedSummary();
                summary.setCountry(country);
                summary.setCarName(carName);
                summary.setGeneratedDate(generatedDate);
                summary.setIsDeleted(0);
                summary.setCreationTime(new Date());
            }

            summary.setRaceDuration(dailyDuration.floatValue());
            summary.setCumulativeRaceDuration(String.format("%.2f", newCumulative));
            summary.setLastModifiedTime(new Date());

            batch.add(summary);
            processed++;

            if (batch.size() >= batchSize) {
                racePlayedSummaryRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            racePlayedSummaryRepository.saveAll(batch);
        }

        log.info("Race Played migration completed: {} records processed", processed);
    }

    private Map<String, Double> getStartingCumulatives(Date startDate) {

        Map<String, Double> map = new HashMap<>();

        log.info("Fetching cumulative race duration before {}", startDate);

        List<Object[]> rows =
                racePlayedRepository.getCumulativeDurationBeforeDate(startDate);

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

