package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.AppLaunchedSummary.Controller;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.AppLaunchedSummary.DTO.AppLaunchedSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.AppLaunchedSummary.Model.AppLaunchedSummary;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.AppLaunchedSummary.Repository.AppLaunchedSummaryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.AppLaunchedSummary.Service.AppLaunchedSummaryService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.AppLaunchedSummary.Service.AppLaunchedSummaryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/shell-analytics/app-launched-summary")
public class AppLaunchedSummaryController extends AbstractCRUDController<AppLaunchedSummary, AppLaunchedSummaryDTO, AppLaunchedSummaryRepository, AppLaunchedSummaryServiceImpl> {
    @Autowired
    AppLaunchedSummaryService appLaunchedSummaryService;

    @PostMapping("/summary-by-date")
    public ResponseEntity<ApplicationResponse> summaryByDate(@RequestBody Map<String, String> req) {
        String date = req.get("date");
        return ResponseEntity.ok(appLaunchedSummaryService.getSummaryByDate(date));
    }

    @PostMapping("/summary-by-date-range")
    public ResponseEntity<ApplicationResponse> getSummariesByUserCountries(@RequestBody Map<String, String> formData) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            String startDateStr = formData.get("startDate");
            String endDateStr = formData.get("endDate");
            String countries = formData.get("country");

            Map<String, Object> data = appLaunchedSummaryService.getSummariesByUserCountries(countries, startDateStr, endDateStr);

            String message;

            if (countries != null && !countries.trim().isEmpty()) {
                message = "Summary fetched successfully for requested countries from " + startDateStr + " to " + endDateStr;
            } else {
                message = "Summary fetched successfully from " + startDateStr + " to " + endDateStr;
            }

            applicationResponse.setData(data);
            applicationResponse.setSuccess(true);
            applicationResponse.setCode(200);
            applicationResponse.setCurrentRecords(((List<?>) data.get("summaryTable")).size());
            applicationResponse.setMessage(message);
            applicationResponse.setError("");

            return ResponseEntity.ok(applicationResponse);

        } catch (Exception e) {
            applicationResponse.setSuccess(false);
            applicationResponse.setError(e.getMessage());
            applicationResponse.setMessage("Failed to fetch summary");
            applicationResponse.setCode(500);
            return ResponseEntity.ok(applicationResponse);
        }
    }

    @PostMapping("/migrate-to-app-launched-summary")
    public ApplicationResponse migrate(@RequestBody Map<String, String> req) {
        String startDate = req.get("startDate");
        String endDate = req.get("endDate");

        if (startDate == null || startDate.isEmpty()) {
            return ApplicationResponse.builder()
                    .success(false)
                    .message("startDate is required")
                    .code(400)
                    .build();
        }

        if (endDate == null || endDate.isEmpty()) {
            return ApplicationResponse.builder()
                    .success(false)
                    .message("endDate is required")
                    .code(400)
                    .build();
        }

        try {
            log.info("Migration request received: startDate={}, endDate={}", startDate, endDate);

            appLaunchedSummaryService.migrateAppLaunchData(startDate, endDate);

            return ApplicationResponse.builder()
                    .success(true)
                    .message("Migration completed successfully from " + startDate + " to " + endDate)
                    .code(200)
                    .build();

        } catch (ParseException e) {
            log.error("Invalid date format", e);
            return ApplicationResponse.builder()
                    .success(false)
                    .message("Invalid date format. Use yyyy-MM-dd")
                    .error(e.getMessage())
                    .code(400)
                    .build();
        } catch (Exception e) {
            log.error("Migration failed", e);
            return ApplicationResponse.builder()
                    .success(false)
                    .message("Migration failed")
                    .error(e.getMessage())
                    .code(500)
                    .build();
        }
    }
}