package com.pcpl.carbon.shell_analytics_service.PlayStoreReport.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.PlayStoreReportGraphDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.PlayStoreReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PlayStoreReportRepository extends PCPLCRUDRepository<PlayStoreReport> {
    @Query("SELECT new com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.PlayStoreReportGraphDTO(" +
            "salesReport.countryCode,salesReport.tenantId,month(salesReport.date), year(salesReport.date), SUM(salesReport.installs),country.countryName) " +
            "FROM PlayStoreReport salesReport " +
            "LEFT JOIN Country country ON salesReport.countryCode = country.country and country.isDeleted=0 "+
            "WHERE salesReport.date BETWEEN :startDate AND :endDate  AND salesReport.isDeleted = 0" +
            "AND (:countries IS NULL OR salesReport.countryCode IN :countries) "+
            "GROUP BY salesReport.countryCode,month(salesReport.date), year(salesReport.date),country.countryName " +
            "ORDER BY salesReport.countryCode,month(salesReport.date), year(salesReport.date)")
    List<PlayStoreReportGraphDTO> getPlayStoreReport(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("countries") List<String> countries);
}
