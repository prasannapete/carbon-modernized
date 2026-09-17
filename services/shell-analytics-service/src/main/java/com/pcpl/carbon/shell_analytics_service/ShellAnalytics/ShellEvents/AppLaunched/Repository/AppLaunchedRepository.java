package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryWiseAppLaunchStatsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.AppLaunched;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface AppLaunchedRepository extends PCPLCRUDRepository<AppLaunched> {
    @Query(
            "SELECT new com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryWiseAppLaunchStatsDTO(" +
            "al.country, CAST(al.creationTime AS date), COUNT(al.id),country.countryName) " +
            "FROM AppLaunched al " +
                    "LEFT JOIN Country country ON al.country = country.country and country.isDeleted=0 "+
                    "WHERE al.isDeleted = 0 and al.creationTime BETWEEN :startDate AND :endDate  " +
                    "AND (:countries IS NULL OR al.country IN :countries) " +
                    "and al.gameId in (SELECT DISTINCT (cu.gameId) as gameId" +
                    "                                                        FROM CarUnlocked cu" +
                    "                                                     Where cu.carName IN (SELECT mc2.carName FROM MasterCars mc2 WHERE mc2.countryCode IN (:countries) and mc2.isActive=1 AND cu.isDeleted=0)" +
                    "                                                           AND cu.creationTime BETWEEN :startDate AND :endDate" +
                    "                                                           AND (cu.country IN :countries))"+
            "GROUP BY al.country,CAST(al.creationTime AS date),country.countryName " +
            "ORDER BY CAST(al.creationTime AS date)")
    List<CountryWiseAppLaunchStatsDTO> getCountryWiseLaunchesBetweenDates(@Param("startDate")Date startDate,@Param("endDate") Date endDate,@Param("countries") List<String> countries);

//    @Query("SELECT al.country, " +
//            "SUM(CASE WHEN al.creationTime BETWEEN :startDate AND :endDate THEN 1 ELSE 0 END) AS launches, " +
//            "COUNT(al) AS cumulative, " +
//            "country.countryName " +
//            "FROM AppLaunched al " +
//            "LEFT JOIN Country country ON al.country = country.country and country.isDeleted=0 "+
//            "WHERE al.isDeleted = 0 " +
//            "and al.gameId in (SELECT DISTINCT (cu.gameId) as gameId" +
//            "                                                        FROM CarUnlocked cu" +
//            "                                                     Where cu.carName IN (SELECT mc2.carName FROM MasterCars mc2 WHERE mc2.countryCode IN (:countries) and mc2.isActive=1 AND cu.isDeleted=0)" +
//            "                                                           AND cu.creationTime BETWEEN :startDate AND :endDate" +
//            "                                                           AND (cu.country IN :countries))"+
//            "AND (:countries IS NULL OR al.country IN :countries) "+
//            "GROUP BY al.country,country.countryName" +
//            " order by country.countryName")
@Query(value ="""
SELECT al.country,
       SUM(CASE
               WHEN CAST(al.creation_time as Date) >= :startDate and
                    cast(al.creation_time as date) >= (SELECT cast(min(cu.creation_time) as date)
                                                       FROM srl_car_unlocked cu
                                                       Where cu.car_name IN (SELECT mc2.car_name
                                                                             FROM srl_master_cars mc2
                                                                             WHERE mc2.country_code IN ((:countries))
                                                                               and mc2.is_active = 1
                                                                               AND mc2.is_deleted = 0)
                                                         And cu.game_id = al.game_id
                                                         and cu.is_deleted = 0
                                                         AND (cu.country IN (:countries))) AND
                    CAST(al.creation_time as date) <= :endDate
                   THEN 1
               ELSE 0 END) AS launches,
       COUNT(al)           AS cumulative,
       country.country_name
FROM srl_app_launched al
         LEFT JOIN srl_country country ON al.country = country.country and country.is_deleted = 0
WHERE al.is_deleted = 0
  and al.game_id in (SELECT DISTINCT (cu.game_id) as gameId
                     FROM srl_car_unlocked cu
                     Where cu.car_name IN (SELECT mc2.car_name
                                           FROM srl_master_cars mc2
                                           WHERE mc2.country_code IN ((:countries))
                                             and mc2.is_active = 1
                                             AND mc2.is_deleted = 0)
                       and cu.is_deleted = 0
                       AND (cu.country IN (:countries)))
  AND ((:countries) IS NULL OR al.country IN (:countries)) and
    CAST(al.creation_time as Date) >= :startDate and CAST(al.creation_time as date) <= :endDate
GROUP BY al.country, country.country_name

        """,nativeQuery = true)

    List<Object[]> getCountryWiseLaunchedSummary(@Param("startDate")Date startDate,@Param("endDate")  Date endDate,@Param("countries") List<String> countries);

//    @Query("SELECT DISTINCT (al.country) " +
//            "FROM AppLaunched al " +
//            "WHERE al.isDeleted = 0 " )
//    List<String> getCountryByAppLaunched();

    @Query("SELECT DISTINCT (al.country) FROM AppLaunched al WHERE (al.isDeleted = 0) AND (al.country IS NOT NULL)")
    List<String> getCountryByAppLaunched();

    @Query("SELECT COUNT(al) FROM AppLaunched al WHERE al.country = :country " +
            "AND CAST(al.creationTime AS LocalDate) = :date " +
            "AND al.isDeleted = 0")
    Long getCountByCountryAndDate(@Param("country") String country, @Param("date") Date date);

    @Query("SELECT COUNT(al) FROM AppLaunched al WHERE al.country = :country " +
            "AND CAST(al.creationTime AS LocalDate) <= :date " +
            "AND al.isDeleted = 0")
    Long getCumulativeCountByCountryUpToDate(@Param("country") String country, @Param("date") Date date);

    @Query("""
        SELECT al.country,
               CAST(al.creationTime AS date) AS launchDate,
               COUNT(al.gameId) AS dailyCount
        FROM AppLaunched al
        WHERE al.isDeleted = 0
          AND al.creationTime >= :startDate
          AND al.creationTime <= :endDate
        GROUP BY CAST(al.creationTime AS date), al.country
        ORDER BY CAST(al.creationTime AS date), al.country
    """)
    List<Object[]> getDailyCounts(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("""
        SELECT al.country,
               COUNT(al.gameId) AS cumulativeCount
        FROM AppLaunched al
        WHERE al.isDeleted = 0
          AND al.creationTime < :beforeDate
        GROUP BY al.country
    """)
    List<Object[]> getCumulativeCountsBeforeDate(@Param("beforeDate") Date beforeDate);
}

