package com.pcpl.carbon.shell_analytics_service.PlayStoreReport.Controller;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.PlayStoreReportDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.PlayStoreReport;
import com.pcpl.carbon.shell_analytics_service.PlayStoreReport.Repository.PlayStoreReportRepository;
import com.pcpl.carbon.shell_analytics_service.PlayStoreReport.Service.PlayStoreReportServiceImpl;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/srl/playStoreReport")
@Slf4j
public class PlayStoreReportController extends AbstractCRUDController<PlayStoreReport, PlayStoreReportDTO, PlayStoreReportRepository, PlayStoreReportServiceImpl> {
    @Autowired
    PlayStoreReportServiceImpl playStoreReportService;

    @RequestMapping(value = "/get-play-store-data", method = RequestMethod.POST)
    public ApplicationResponse getPlayStoreData(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = playStoreReportService.fetchPlayStoreSalesReport(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

}
