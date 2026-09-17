package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppStoreSalesReport.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.AppStoreSalesReportGraphDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CarUnlockedGraphDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.AppStoreSalesReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface AppStoreSalesReportRepository extends PCPLCRUDRepository<AppStoreSalesReport> {
    @Query("SELECT new com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.AppStoreSalesReportGraphDTO(" +
            "salesReport.countryCode,month(salesReport.beginDate), year(salesReport.beginDate), COUNT(salesReport),country.countryName) " +
            "FROM AppStoreSalesReport salesReport " +
            "LEFT JOIN Country country ON salesReport.countryCode = country.country and country.isDeleted=0 "+
            "WHERE salesReport.beginDate BETWEEN :startDate AND :endDate  AND salesReport.isDeleted = 0" +
            "AND (:countries IS NULL OR salesReport.countryCode IN :countries) "+
            "GROUP BY salesReport.countryCode,month(salesReport.beginDate), year(salesReport.beginDate), country.countryName " +
            "ORDER BY salesReport.countryCode,month(salesReport.beginDate), year(salesReport.beginDate)")
    List<AppStoreSalesReportGraphDTO> getAppSoreSalesReportByDateRange(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("countries") List<String> countries);
}
