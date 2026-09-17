package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RacePlayedSummary.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.RacePlayedSummary.Model.RacePlayedSummary;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.RacePlayedSummary.DTO.RacePlayedSummaryDTO;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RacePlayedSummary.Repository.RacePlayedSummaryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RacePlayedSummary.Service.RacePlayedSummaryService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RacePlayedSummary.Service.RacePlayedSummaryServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/shell-analytics/race-played-summary")

public class RacePlayedSummaryController extends AbstractCRUDController<RacePlayedSummary, RacePlayedSummaryDTO, RacePlayedSummaryRepository, RacePlayedSummaryServiceImpl> {

    @Autowired
    RacePlayedSummaryService racePlayedSummaryService;

    @PostMapping("/summary-by-date")
    public ResponseEntity<ApplicationResponse> summaryByDate(@RequestBody Map<String, String> req) {
        String date = req.get("date");
        return ResponseEntity.ok(racePlayedSummaryService.getSummaryByDate(date));
    }

    @PostMapping("/summary-by-date-range")
    public ResponseEntity<ApplicationResponse> summaryByDateRange(@RequestBody Map<String, String> req) {

        ApplicationResponse response = ApplicationResponse.builder().build();

        try {
            String startDate = req.get("startDate");
            String endDate   = req.get("endDate");
            String country   = req.get("country");

            Map<String, Object> data = racePlayedSummaryService.getSummaryByDateRange(startDate, endDate, country);

            response.setData(data);
            response.setSuccess(true);
            response.setCode(200);
            response.setCurrentRecords(((List<?>) data.get("summaryTable")).size());

            if (country != null && !country.trim().isEmpty()) {
                response.setMessage("Summary fetched successfully for allowed matching countries from "
                        + startDate + " to " + endDate);
            } else {
                response.setMessage("Summary fetched successfully from " + startDate + " to " + endDate);
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setCode(500);
            response.setMessage("Failed to fetch summary");
            response.setError(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/migrate-to-race-played-summary")
    public ApplicationResponse migrate(@RequestBody Map<String, String> req) {

        String startDate = req.get("startDate");
        String endDate = req.get("endDate");

        if (startDate == null || endDate == null) {
            return ApplicationResponse.builder()
                    .success(false)
                    .message("startDate and endDate are required")
                    .code(400)
                    .build();
        }

        try {
            racePlayedSummaryService.migrateRacePlayedData(startDate, endDate);

            return ApplicationResponse.builder()
                    .success(true)
                    .message("Migration completed successfully from " + startDate + " to " + endDate)
                    .code(200)
                    .build();

        } catch (Exception e) {
            log.error("Race Played migration failed", e);
            return ApplicationResponse.builder()
                    .success(false)
                    .message("Race Played migration failed")
                    .error(e.getMessage())
                    .code(500)
                    .build();
        }
    }
}


