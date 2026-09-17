package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.ShellEventsDataDTO;
import jakarta.mail.Multipart;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ShellEventsService {
    public ApplicationResponse saveObject(String key, ShellEventsDataDTO shellEventsDataDTO);
    public ApplicationResponse getObject(String key);
    public ApplicationResponse saveShellEvents(ShellEventsDataDTO shellEventsDataDTO);
    public ApplicationResponse getAppId(String appId, String jwtToken) throws Exception;
    public ApplicationResponse fetchSaleReport( String jwtToken,String date) throws Exception;
    public ApplicationResponse playStoreReport() throws Exception;

    public ApplicationResponse importPlayStoreDataByCSV(MultipartFile multipartFile) throws Exception;

    public ApplicationResponse exportMultiTabExcel(HttpServletResponse response, String startDate, String endDate, String country) throws Exception;

}
