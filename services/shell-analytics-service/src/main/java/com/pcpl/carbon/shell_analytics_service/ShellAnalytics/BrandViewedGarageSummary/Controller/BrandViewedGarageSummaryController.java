package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.BrandViewedGarageSummary.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.BrandViewedGarageSummary.DTO.BrandViewedGarageSummaryDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.BrandViewedGarageSummary.Model.BrandViewedGarageSummary;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.BrandViewedGarageSummary.Repository.BrandViewedGarageSummaryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.BrandViewedGarageSummary.Service.BrandViewedGarageSummaryService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.BrandViewedGarageSummary.Service.BrandViewedGarageSummaryServiceImpl;
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
@RequestMapping(value = "/shell-analytics/brand-viewed-garage-summary")
@Slf4j
public class BrandViewedGarageSummaryController extends AbstractCRUDController<BrandViewedGarageSummary, BrandViewedGarageSummaryDTO, BrandViewedGarageSummaryRepository, BrandViewedGarageSummaryServiceImpl> {

    @Autowired
    BrandViewedGarageSummaryService brandViewedGarageSummaryService;

    @PostMapping("/summary-by-date")
    public ResponseEntity<ApplicationResponse> summaryByDate(@RequestBody Map<String, String> req) {
        String date = req.get("date");
        return ResponseEntity.ok(brandViewedGarageSummaryService.getSummaryByDate(date));
    }

    @PostMapping("/summary-by-date-range")
    public ResponseEntity<ApplicationResponse> summaryByDateRange(@RequestBody Map<String, String> req) {

        ApplicationResponse response = ApplicationResponse.builder().build();

        try {
            String startDate = req.get("startDate");
            String endDate   = req.get("endDate");
            String country   = req.get("country");

            Map<String, Object> data = brandViewedGarageSummaryService.getSummariesByUserCountries(country, startDate, endDate);

            int recordCount = ((List<?>) data.get("summaryTable")).size();

            String message = (country != null && !country.trim().isEmpty())
                    ? "Summary fetched successfully for requested countries from " + startDate + " to " + endDate
                    : "Summary fetched successfully from " + startDate + " to " + endDate;

            response.setSuccess(true);
            response.setCode(200);
            response.setMessage(message);
            response.setData(data);
            response.setCurrentRecords(recordCount);
            response.setError("");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setCode(500);
            response.setMessage("Failed to fetch summary");
            response.setError(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/migrate-to-brand-viewed-summary")
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
            log.info("Brand Viewed migration request received: startDate={}, endDate={}",
                    startDate, endDate);

            brandViewedGarageSummaryService.migrateBrandViewedData(startDate, endDate);

            return ApplicationResponse.builder()
                    .success(true)
                    .message("Brand Viewed Garage migration completed successfully from " +
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
            log.error("Brand Viewed migration failed", e);
            return ApplicationResponse.builder()
                    .success(false)
                    .message("Brand Viewed Garage migration failed")
                    .error(e.getMessage())
                    .code(500)
                    .build();
        }
    }

}
