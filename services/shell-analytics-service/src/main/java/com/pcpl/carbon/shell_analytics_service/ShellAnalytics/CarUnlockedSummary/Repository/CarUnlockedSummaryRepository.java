package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.CarUnlockedSummary.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.CarUnlockedSummary.Model.CarUnlockedSummary;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface CarUnlockedSummaryRepository extends PCPLCRUDRepository<CarUnlockedSummary> {

    @Query("SELECT s.country, s.carName, MAX(s.generatedDate), SUM(s.unlockedCount), MAX(s.cumulativeUnlockedCount) " +
            "FROM CarUnlockedSummary s " +
            "WHERE CAST(s.generatedDate AS LocalDate) = :givenDate " +
            "AND s.isDeleted = 0 AND s.country IS NOT NULL AND s.unlockedCount > 0 " +
            "GROUP BY s.country, s.carName " +
            "ORDER BY s.country, s.carName")
    List<Object[]> getSummaryByDate(@Param("givenDate") LocalDate givenDate);

    @Query("SELECT s FROM CarUnlockedSummary s " +
            "WHERE s.country = :country AND s.carName = :carName " +
            "AND CAST(s.generatedDate AS date) = CAST(:date AS date) AND s.isDeleted = 0 " +
            "ORDER BY s.id DESC")
    List<CarUnlockedSummary> getSummaryByCountryCarAndDate(@Param("country") String country, @Param("carName") String carName, @Param("date") Date date);

    @Query(
            "SELECT " +
                    " s.country, " +
                    " s.carName, " +
                    " c.countryName, " +
                    " COALESCE(mc.isActive, 0), " +
                    " SUM(s.unlockedCount) " +
                    "FROM CarUnlockedSummary s " +
                    "LEFT JOIN Country c ON s.country = c.country AND c.isDeleted = 0 " +
                    "LEFT JOIN MasterCars mc ON mc.carName = s.carName AND mc.countryCode = s.country AND mc.isActive = 1 " +
                    "WHERE s.isDeleted = 0 " +
                    "AND s.generatedDate >= :startDate\n" +
                    "  AND s.generatedDate < :endDate"+
                    "  AND (:countries IS NULL OR s.country IN :countries) " +
                    "  AND (" +
                    "         (s.country IN (SELECT DISTINCT m.countryCode FROM MasterCars m) AND mc.id IS NOT NULL) " +
                    "         OR " +
                    "         (s.country NOT IN (SELECT DISTINCT m2.countryCode FROM MasterCars m2))" +
                    "      ) " +
                    "GROUP BY s.country, s.carName, c.countryName, mc.isActive " +
                    "ORDER BY s.country, s.carName"
    )
    List<Object[]> getUnlockedCountsPerCar(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("countries") List<String> countries
    );


    @Query(
            "SELECT s.country, s.carName, s.cumulativeUnlockedCount " +
                    "FROM CarUnlockedSummary s " +
                    "WHERE s.isDeleted = 0 " +
                    "AND (:countries IS NULL OR s.country IN :countries) " +
                    "AND s.generatedDate = (" +
                    "    SELECT MAX(s2.generatedDate) " +
                    "    FROM CarUnlockedSummary s2 " +
                    "    WHERE s2.country = s.country " +
                    "    AND s2.carName = s.carName " +
                    "    AND s2.isDeleted = 0" +
                    ") " +
                    "ORDER BY s.country, s.carName"
    )
    List<Object[]> getLatestCumulativeCounts(
            @Param("countries") List<String> countries
    );

    @Query("""
        SELECT s FROM CarUnlockedSummary s
        WHERE s.country = :country
          AND s.carName = :carName
          AND s.generatedDate = :date
          AND s.isDeleted = 0
    """)
    List<CarUnlockedSummary> findByCountryCarNameAndDate(
            @Param("country") String country,
            @Param("carName") String carName,
            @Param("date") Date date
    );

}
