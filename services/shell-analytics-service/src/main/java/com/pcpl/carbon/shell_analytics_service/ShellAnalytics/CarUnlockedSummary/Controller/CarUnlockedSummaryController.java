package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.CarUnlockedSummary.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.CarUnlockedSummary.DTO.CarUnlockedSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.CarUnlockedSummary.Model.CarUnlockedSummary;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.CarUnlockedSummary.Repository.CarUnlockedSummaryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.CarUnlockedSummary.Service.CarUnlockedSummaryService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.CarUnlockedSummary.Service.CarUnlockedSummaryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/shell-analytics/car-unlocked-summary")
@Slf4j
public class CarUnlockedSummaryController extends AbstractCRUDController<CarUnlockedSummary, CarUnlockedSummaryDTO, CarUnlockedSummaryRepository, CarUnlockedSummaryServiceImpl> {
    @Autowired
    CarUnlockedSummaryService carUnlockedSummaryService;

    @PostMapping("/summary-by-date")
    public ResponseEntity<ApplicationResponse> summaryByDate(@RequestBody Map<String, String> req) {
        String date = req.get("date");
        return ResponseEntity.ok(carUnlockedSummaryService.getSummaryByDate(date));
    }
    @PostMapping("/summary-by-date-range")
    public ResponseEntity<ApplicationResponse> summaryByDateRange(@RequestBody Map<String, String> req) {

        String startDate = req.get("startDate");
        String endDate   = req.get("endDate");
        String country   = req.get("country");

        ApplicationResponse response = ApplicationResponse.builder().build();

        try {
            Map<String, Object> data =
                    carUnlockedSummaryService.getSummaryByDateRange(startDate, endDate, country);

            response.setData(data);
            response.setSuccess(true);
            response.setCode(200);
            response.setCurrentRecords(((List<?>) data.get("summaryTable")).size());

            if (country != null && !country.trim().isEmpty()) {
                response.setMessage("Summary fetched successfully for requested countries from "
                        + startDate + " to " + endDate);
            } else {
                response.setMessage("Summary fetched successfully from "
                        + startDate + " to " + endDate);
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setCode(500);
            response.setMessage("Failed to fetch summary");
            response.setError(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/migrate-to-car-unlocked-summary")
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
            log.info("Car Unlocked migration request received: startDate={}, endDate={}",
                    startDate, endDate);

            carUnlockedSummaryService.migrateCarUnlockedData(startDate, endDate);

            return ApplicationResponse.builder()
                    .success(true)
                    .message("Car Unlocked migration completed successfully from " +
                            startDate + " to " + endDate)
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
            log.error("Car Unlocked migration failed", e);
            return ApplicationResponse.builder()
                    .success(false)
                    .message("Car Unlocked migration failed")
                    .error(e.getMessage())
                    .code(500)
                    .build();
        }
    }

}

