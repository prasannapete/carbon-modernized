package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppStoreSalesReport.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.AppStoreSalesReportDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.AppStoreSalesReport;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppStoreSalesReport.Repository.AppStoreSalesReportRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppStoreSalesReport.Service.AppStoreSalesReportService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppStoreSalesReport.Service.AppStoreSalesServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/srl/AppStoreSaleRep")
public class AppStoreSalesReportController extends AbstractCRUDController<AppStoreSalesReport, AppStoreSalesReportDTO, AppStoreSalesReportRepository, AppStoreSalesServiceImpl> {
    @Autowired
    AppStoreSalesReportService appStoreSalesService;

    @RequestMapping(value = "/get-app-store-data", method = RequestMethod.POST)
    public ApplicationResponse getAppStoreData(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = appStoreSalesService.fetchAppStoreSalesReport(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
