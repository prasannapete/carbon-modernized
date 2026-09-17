package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.RacePlayedSummary.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.RacePlayedSummary.Model.RacePlayedSummary;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface RacePlayedSummaryRepository extends PCPLCRUDRepository<RacePlayedSummary> {

    @Query("SELECT s.country, s.carName, MAX(s.generatedDate), SUM(s.raceDuration), MAX(s.cumulativeRaceDuration) " +
            "FROM RacePlayedSummary s " +
            "WHERE CAST(s.generatedDate AS LocalDate) = :givenDate " +
            "AND s.isDeleted = 0 AND s.country IS NOT NULL AND s.raceDuration > 0 " +
            "GROUP BY s.country, s.carName " +
            "ORDER BY s.country, s.carName")
    List<Object[]> getSummaryByDate(@Param("givenDate") LocalDate givenDate);

    @Query("SELECT s FROM RacePlayedSummary s " +
            "WHERE s.country = :country AND s.carName = :carName " +
            "AND CAST(s.generatedDate AS date) = CAST(:date AS date) AND s.isDeleted = 0 " +
            "ORDER BY s.id DESC")
    List<RacePlayedSummary> getSummaryByCountryCarAndDate(@Param("country") String country, @Param("carName") String carName, @Param("date") Date date);

    @Query("SELECT s.country, s.carName, c.countryName, COALESCE(mc.isActive, 0), " +
            "SUM(s.raceDuration) " +
            "FROM RacePlayedSummary s " +
            "LEFT JOIN Country c ON s.country = c.country AND c.isDeleted = 0 " +
            "INNER JOIN MasterCars mc ON mc.carName = s.carName AND mc.countryCode = s.country AND mc.isActive = 1 " +
            "WHERE s.generatedDate >= :fromDate " +
            "AND s.generatedDate < :toDate " +
            "AND s.isDeleted = 0 " +
            "AND s.country IS NOT NULL " +
            "AND s.raceDuration > 0 " +
            "AND (:countries IS NULL OR s.country IN :countries) " +
            "AND s.carName IN (" +
            "    SELECT mc2.carName FROM MasterCars mc2 " +
            "    WHERE (:countries IS NULL OR mc2.countryCode IN :countries) " +
            "    AND mc2.isActive = 1" +
            ") " +
            "GROUP BY s.country, s.carName, c.countryName, mc.isActive " +
            "ORDER BY s.country, s.carName")
    List<Object[]> getSummariesByDateRange(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("countries") List<String> countries
    );

    @Query(
            "SELECT s.country, s.carName, s.cumulativeRaceDuration " +
                    "FROM RacePlayedSummary s " +
                    "WHERE s.isDeleted = 0 " +
                    "AND (:countries IS NULL OR s.country IN :countries) " +
                    "AND s.generatedDate = (" +
                    "    SELECT MAX(s2.generatedDate) FROM RacePlayedSummary s2 " +
                    "    WHERE s2.country = s.country " +
                    "    AND s2.carName = s.carName " +
                    "    AND s2.isDeleted = 0" +
                    ")"
    )
    List<Object[]> getLatestCumulativeDurations(
            @Param("countries") List<String> countries
    );

    @Query("""
        SELECT s FROM RacePlayedSummary s
        WHERE s.country = :country
          AND s.carName = :carName
          AND s.generatedDate = :date
          AND s.isDeleted = 0
    """)
    List<RacePlayedSummary> findByCountryCarAndDate(
            @Param("country") String country,
            @Param("carName") String carName,
            @Param("date") Date date
    );
}
