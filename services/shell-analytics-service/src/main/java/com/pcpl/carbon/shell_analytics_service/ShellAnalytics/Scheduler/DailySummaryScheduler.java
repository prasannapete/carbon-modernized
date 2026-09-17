package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.Scheduler;

import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.AppLaunchedSummary.Service.AppLaunchedSummaryService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.BrandViewedGarageSummary.Service.BrandViewedGarageSummaryService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.CarUnlockedSummary.Service.CarUnlockedSummaryService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RCPlayedSummary.Service.RCPlayedSummaryService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RacePlayedSummary.Service.RacePlayedSummaryService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Repository.AppLaunchedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.BrandViewedGarage.Repository.BrandViewedGarageRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CarUnlocked.Repository.CarUnlockedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RCPlayed.Repository.RCPlayedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RacePlayed.Repository.RacePlayedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class DailySummaryScheduler  {

    @Autowired
    AppLaunchedSummaryService appLaunchedSummaryService;

    @Autowired
    BrandViewedGarageSummaryService brandViewedGarageSummaryService;

    @Autowired
    CarUnlockedSummaryService carUnlockedSummaryService;

    @Autowired
    RacePlayedSummaryService racePlayedSummaryService;

    @Autowired
    RCPlayedSummaryService rcPlayedSummaryService;

    @Autowired
    AppLaunchedRepository appLaunchedRepository;

    @Autowired
    BrandViewedGarageRepository brandViewedGarageRepository;

    @Autowired
    CarUnlockedRepository carUnlockedRepository;

    @Autowired
    RacePlayedRepository racePlayedRepository;

    @Autowired
    RCPlayedRepository rcPlayedRepository;

    @Scheduled(cron = "0 59 23 * * *")
    public void generateAllDailySummaries() {

        Date summaryDate = new Date();

        log.info("=== Starting Daily Summary Generation for date: {} ===", summaryDate);

        generateAppLaunchedSummary(summaryDate);
        generateBrandViewedGarageSummary(summaryDate);
        generateCarUnlockedSummary(summaryDate);
        generateRacePlayedSummary(summaryDate);
        generateRCPlayedSummary(summaryDate);

        log.info("=== Completed Daily Summary Generation for date: {} ===", summaryDate);
    }

    private void generateAppLaunchedSummary(Date summaryDate) {
        try {
            List<String> countries = appLaunchedRepository.getCountryByAppLaunched();
            if (countries == null || countries.isEmpty()) {
                return;
            }
            for (String country : countries) {
                try {
                    appLaunchedSummaryService.generateEndOfDaySummaryForCountry(country, summaryDate);
                } catch (Exception e) {
                    log.error("Failed to generate AppLaunched summary for country: {} on date: {}", country, summaryDate, e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to generate AppLaunched summaries for date: {}", summaryDate, e);
        }
    }

    private void generateBrandViewedGarageSummary(Date summaryDate) {
        try {
            List<String> countries = brandViewedGarageRepository.getCountryByBrandViewedGarage();
            if (countries == null || countries.isEmpty()) {
                return;
            }
            for (String country : countries) {
                try {
                    brandViewedGarageSummaryService.generateEndOfDaySummaryForCountry(country, summaryDate);
                } catch (Exception e) {
                    log.error("Failed to generate BrandViewedGarage summary for country: {} on date: {}", country, summaryDate, e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to generate BrandViewedGarage summaries for date: {}", summaryDate, e);
        }
    }

    private void generateCarUnlockedSummary(Date summaryDate) {
        try {
            List<String> countries = carUnlockedRepository.getCountryByCarUnlocked();
            if (countries == null || countries.isEmpty()) {
                return;
            }
            for (String country : countries) {
                try {
                    carUnlockedSummaryService.generateEndOfDaySummaryForCountry(country, summaryDate);
                } catch (Exception e) {
                    log.error("Failed to generate CarUnlocked summary for country: {} on date: {}", country, summaryDate, e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to generate CarUnlocked summaries for date: {}", summaryDate, e);
        }
    }

    private void generateRacePlayedSummary(Date summaryDate) {
        try {
            List<String> countries = racePlayedRepository.getCountryByRacePlayed();
            if (countries == null || countries.isEmpty()) {
                return;
            }
            for (String country : countries) {
                try {
                    racePlayedSummaryService.generateEndOfDaySummaryForCountry(country, summaryDate);
                } catch (Exception e) {
                    log.error("Failed to generate RacePlayed summary for country: {} on date: {}", country, summaryDate, e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to generate RacePlayed summaries for date: {}", summaryDate, e);
        }
    }

    private void generateRCPlayedSummary(Date summaryDate) {
        try {
            List<String> countries = rcPlayedRepository.getCountryByRCPlayed();
            if (countries == null || countries.isEmpty()) {
                return;
            }
            for (String country : countries) {
                try {
                    rcPlayedSummaryService.generateEndOfDaySummaryForCountry(country, summaryDate);
                } catch (Exception e) {
                    log.error("Failed to generate RCPlayed summary for country: {} on date: {}", country, summaryDate, e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to generate RCPlayed summaries for date: {}", summaryDate, e);
        }
    }
}
