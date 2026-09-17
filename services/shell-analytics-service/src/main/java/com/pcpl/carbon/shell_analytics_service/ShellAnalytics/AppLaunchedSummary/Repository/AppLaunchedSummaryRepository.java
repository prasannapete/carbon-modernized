package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.AppLaunchedSummary.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.AppLaunchedSummary.Model.AppLaunchedSummary;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Repository
public interface AppLaunchedSummaryRepository extends PCPLCRUDRepository<AppLaunchedSummary> {

    @Query("SELECT s.country, MAX(s.generatedDate), SUM(s.appLaunchedCount), MAX(s.cumulativeAppLaunchedCount) " +
            "FROM AppLaunchedSummary s " +
            "WHERE CAST(s.generatedDate AS LocalDate) = :givenDate " +
            "AND s.isDeleted = 0 AND s.country IS NOT NULL AND s.appLaunchedCount > 0 " +
            "GROUP BY s.country " +
            "ORDER BY s.country")
    List<Object[]> getSummaryByDate(@Param("givenDate") LocalDate givenDate);

    @Query("SELECT s FROM AppLaunchedSummary s " +
            "WHERE s.country = :country " +
            "AND CAST(s.generatedDate AS date) = CAST(:date AS date) " +
            "AND s.isDeleted = 0 " +
            "ORDER BY s.id DESC")
    List<AppLaunchedSummary> getSummaryByCountryAndDate(@Param("country") String country, @Param("date") Date date);

    @Query("SELECT s, c.countryName FROM AppLaunchedSummary s " +
            "LEFT JOIN Country c ON s.country = c.country AND c.isDeleted = 0 " +
            "WHERE s.generatedDate >= :startDate " +
            "AND s.generatedDate < :endDate " +
            "AND s.isDeleted = 0 AND s.country IS NOT NULL " +
            "AND (:countries IS NULL OR s.country IN :countries) " +
            "ORDER BY s.country")
    List<Object[]> getSummariesByDateRange(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("countries") List<String> countries
    );

    @Query("SELECT s.country, s.cumulativeAppLaunchedCount " +
            "FROM AppLaunchedSummary s " +
            "INNER JOIN (" +
            "  SELECT country AS country, MAX(generatedDate) AS maxDate " +
            "  FROM AppLaunchedSummary " +
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
        SELECT s FROM AppLaunchedSummary s
        WHERE s.country = :country
          AND s.generatedDate = :date
          AND s.isDeleted = 0
    """)
    List<AppLaunchedSummary> findByCountryAndDate(
            @Param("country") String country,
            @Param("date") Date date
    );
}

