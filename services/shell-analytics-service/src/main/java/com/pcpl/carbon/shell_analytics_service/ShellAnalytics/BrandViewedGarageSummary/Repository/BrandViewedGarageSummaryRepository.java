package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.BrandViewedGarageSummary.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.BrandViewedGarageSummary.Model.BrandViewedGarageSummary;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface BrandViewedGarageSummaryRepository extends PCPLCRUDRepository<BrandViewedGarageSummary> {

    @Query("SELECT s.country, MAX(s.generatedDate), SUM(s.brandViewedCount), MAX(s.cumulativeBrandViewedCount) " +
            "FROM BrandViewedGarageSummary s " +
            "WHERE CAST(s.generatedDate AS LocalDate) = :givenDate " +
            "AND s.isDeleted = 0 AND s.country IS NOT NULL AND s.brandViewedCount > 0 " +
            "GROUP BY s.country " +
            "ORDER BY s.country")
    List<Object[]> getSummaryByDate(@Param("givenDate") LocalDate givenDate);

    @Query("SELECT s FROM BrandViewedGarageSummary s " +
            "WHERE s.country = :country AND CAST(s.generatedDate AS date) = CAST(:date AS date) " +
            "AND s.isDeleted = 0 ORDER BY s.id DESC")
    List<BrandViewedGarageSummary> getSummaryByCountryAndDate(@Param("country") String country, @Param("date") Date date);

    @Query("SELECT s, c.countryName FROM BrandViewedGarageSummary s " +
            "LEFT JOIN Country c ON s.country = c.country AND c.isDeleted = 0 " +
            "WHERE s.generatedDate >= :startDate " +
            "AND s.generatedDate < :endDate " +
            "AND s.isDeleted = 0 AND s.country IS NOT NULL " +
            "AND (:countries IS NULL OR s.country IN :countries) " +
            "ORDER BY s.country")
    List<Object[]> getSummariesByDateRange(
            @Param("fromDate") Date startDate,
            @Param("toDate") Date endDate,
            @Param("countries") List<String> countries
    );

    @Query("SELECT s.country, s.cumulativeBrandViewedCount " +
            "FROM BrandViewedGarageSummary s " +
            "INNER JOIN (" +
            "  SELECT country AS country, MAX(generatedDate) AS maxDate " +
            "  FROM BrandViewedGarageSummary " +
            "  WHERE isDeleted = 0 " +
            "  GROUP BY country" +
            ") latest ON s.country = latest.country AND s.generatedDate = latest.maxDate " +
            "WHERE s.isDeleted = 0 " +
            "AND (:countries IS NULL OR s.country IN :countries) " +
            "ORDER BY s.country")
    List<Object[]> getLatestCumulativeCounts(
            @Param("countries") List<String> countries
    );

    @Query("""
        SELECT s FROM BrandViewedGarageSummary s
        WHERE s.country = :country
          AND s.generatedDate = :date
          AND s.isDeleted = 0
    """)
    List<BrandViewedGarageSummary> findByCountryAndDate(
            @Param("country") String country,
            @Param("date") Date date
    );
}
