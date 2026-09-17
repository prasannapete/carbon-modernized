package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RCPlayedSummary.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.RCPlayedSummary.DTO.RCPlayedSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.RCPlayedSummary.Model.RCPlayedSummary;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RCPlayedSummary.Repository.RCPlayedSummaryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RCPlayedSummary.Service.RCPlayedSummaryService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RCPlayedSummary.Service.RCPlayedSummaryServiceImpl;
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
@RequestMapping(value = "/shell-analytics/rc-played-summary")
public class RCPlayedSummaryController extends AbstractCRUDController<RCPlayedSummary, RCPlayedSummaryDTO, RCPlayedSummaryRepository, RCPlayedSummaryServiceImpl> {

    @Autowired
    RCPlayedSummaryService rcPlayedSummaryService;

    @PostMapping("/summary-by-date")
    public ResponseEntity<ApplicationResponse> summaryByDate(@RequestBody Map<String, String> req) {
        String date = req.get("date");
        return ResponseEntity.ok(rcPlayedSummaryService.getSummaryByDate(date));
    }

    @PostMapping("/summary-by-date-range")
    public ResponseEntity<ApplicationResponse> summaryByDateRange(@RequestBody Map<String, String> req) {

        ApplicationResponse response = ApplicationResponse.builder().build();

        try {
            String startDate = req.get("startDate");
            String endDate   = req.get("endDate");
            String country   = req.get("country");

            Map<String, Object> data = rcPlayedSummaryService.getSummaryByDateRange(startDate, endDate, country);

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


    @PostMapping("/migrate-to-rc-played-summary")
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
            rcPlayedSummaryService.migrateRCPlayedData(startDate, endDate);

            return ApplicationResponse.builder()
                    .success(true)
                    .message("Migration completed successfully from " + startDate + " to " + endDate)
                    .code(200)
                    .build();

        } catch (Exception e) {
            log.error("RC Played migration failed", e);
            return ApplicationResponse.builder()
                    .success(false)
                    .message("RC Played migration failed")
                    .error(e.getMessage())
                    .code(500)
                    .build();
        }
    }

}
