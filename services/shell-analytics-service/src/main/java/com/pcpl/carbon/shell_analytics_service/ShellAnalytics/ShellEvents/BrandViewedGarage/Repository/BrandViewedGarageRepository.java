package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.BrandViewedGarage.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryWiseBrandViewsStatsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.BrandViewedGarage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface BrandViewedGarageRepository extends PCPLCRUDRepository<BrandViewedGarage> {
    @Query(
            "SELECT new com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryWiseBrandViewsStatsDTO(" +
                    "brandViewedGarage.country, CAST(brandViewedGarage.creationTime AS date), COUNT(brandViewedGarage.id),country.countryName) " +
                    "FROM BrandViewedGarage brandViewedGarage " +
                    "LEFT JOIN Country country ON brandViewedGarage.country = country.country and country.isDeleted=0 "+
                    "WHERE brandViewedGarage.isDeleted = 0 and brandViewedGarage.creationTime BETWEEN :startDate AND :endDate  " +
                    "and brandViewedGarage.gameId in (SELECT DISTINCT (cu.gameId) as gameId" +
                    "                         FROM CarUnlocked cu" +
                    "                         Where cu.carName IN (SELECT mc2.carName FROM MasterCars mc2 WHERE mc2.countryCode IN (:countries) and mc2.isActive=1 AND cu.isDeleted=0)" +
                    "                           AND cu.creationTime BETWEEN :startDate AND :endDate" +
                    "                           AND (cu.country IN :countries))"+
                    "AND (:countries IS NULL OR brandViewedGarage.country IN :countries) "+
                    "GROUP BY brandViewedGarage.country,CAST(brandViewedGarage.creationTime AS date),country.countryName " +
                    "ORDER BY CAST(brandViewedGarage.creationTime AS date)")
    List<CountryWiseBrandViewsStatsDTO> getCountryWiseViewsBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("countries") List<String> countries);

//    @Query("SELECT brandViewedGarage.country, " +
//            "SUM(CASE WHEN brandViewedGarage.creationTime BETWEEN :startDate AND :endDate THEN 1 ELSE 0 END) AS views, " +
//            "COUNT(brandViewedGarage) AS cumulative, " +
//            "country.countryName "+
//            "FROM BrandViewedGarage brandViewedGarage " +
//            "LEFT JOIN Country country ON brandViewedGarage.country = country.country and country.isDeleted=0 "+
//            "WHERE brandViewedGarage.isDeleted = 0 " +
//            "and brandViewedGarage.gameId in (SELECT DISTINCT (cu.gameId) as gameId" +
//            "                                            FROM CarUnlocked cu" +
//            "                                         Where cu.carName IN (SELECT mc2.carName FROM MasterCars mc2 WHERE mc2.countryCode IN (:countries) and mc2.isActive=1 AND cu.isDeleted=0)" +
//            "                                               AND cu.creationTime BETWEEN :startDate AND :endDate" +
//            "                                               AND (cu.country IN :countries))"+
//            "AND (:countries IS NULL OR brandViewedGarage.country IN :countries) "+
//            "GROUP BY brandViewedGarage.country,country.countryName" +
//            " order by country.countryName")
@Query(value ="""
SELECT brandViewedGarage.country,
       SUM(CASE
               WHEN CAST(brandViewedGarage.creation_time as Date) >= :startDate and
                    cast(brandViewedGarage.creation_time as date) >= (SELECT cast(min(cu.creation_time) as date)
                                                       FROM srl_car_unlocked cu
                                                       Where cu.car_name IN (SELECT mc2.car_name
                                                                             FROM srl_master_cars mc2
                                                                             WHERE mc2.country_code IN ((:countries))
                                                                               and mc2.is_active = 1
                                                                               AND mc2.is_deleted = 0)
                                                         And cu.game_id = brandViewedGarage.game_id
                                                         and cu.is_deleted = 0
                                                         AND (cu.country IN (:countries))) AND
                    CAST(brandViewedGarage.creation_time as date) <= :endDate
                   THEN 1
               ELSE 0 END) AS views,
       COUNT(brandViewedGarage)           AS cumulative,
       country.country_name
FROM srl_brand_viewed_garage brandViewedGarage
         LEFT JOIN srl_country country ON brandViewedGarage.country = country.country and country.is_deleted = 0
WHERE brandViewedGarage.is_deleted = 0
  and brandViewedGarage.game_id in (SELECT DISTINCT (cu.game_id) as gameId
                     FROM srl_car_unlocked cu
                     Where cu.car_name IN (SELECT mc2.car_name
                                           FROM srl_master_cars mc2
                                           WHERE mc2.country_code IN ((:countries))
                                             and mc2.is_active = 1
                                             AND mc2.is_deleted = 0)
                       and cu.is_deleted = 0
                       AND (cu.country IN (:countries)))
  AND ((:countries) IS NULL OR brandViewedGarage.country IN (:countries)) and
    CAST(brandViewedGarage.creation_time as Date) >= :startDate and CAST(brandViewedGarage.creation_time as date) <= :endDate
GROUP BY brandViewedGarage.country, country.country_name

        """,nativeQuery = true)
    List<Object[]> getCountryWiseViewsSummary(@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("countries") List<String> countries);


    @Query("SELECT DISTINCT b.country FROM BrandViewedGarage b WHERE b.isDeleted = 0 AND b.country IS NOT NULL")
    List<String> getCountryByBrandViewedGarage();

    @Query("SELECT COUNT(b) FROM BrandViewedGarage b WHERE b.country = :country " +
            "AND CAST(b.creationTime AS LocalDate) = :date AND b.isDeleted = 0")
    Long getCountByCountryAndDate(@Param("country") String country, @Param("date") Date date);

    @Query("SELECT COUNT(b) FROM BrandViewedGarage b WHERE b.country = :country " +
            "AND CAST(b.creationTime AS LocalDate) <= :date AND b.isDeleted = 0")
    Long getCumulativeCountByCountryUpToDate(@Param("country") String country, @Param("date") Date date);



    @Query("""
        SELECT bvg.country,
               CAST(bvg.creationTime AS date) AS viewedDate,
               COUNT(bvg.gameId) AS dailyCount
        FROM BrandViewedGarage bvg
        WHERE bvg.isDeleted = 0
          AND bvg.creationTime >= :startDate
          AND bvg.creationTime <= :endDate
        GROUP BY CAST(bvg.creationTime AS date), bvg.country
        ORDER BY CAST(bvg.creationTime AS date), bvg.country
    """)
    List<Object[]> getDailyCounts(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );
    @Query("""
        SELECT bvg.country,
               COUNT(bvg.gameId) AS cumulativeCount
        FROM BrandViewedGarage bvg
        WHERE bvg.isDeleted = 0
          AND bvg.creationTime < :beforeDate
        GROUP BY bvg.country
    """)
    List<Object[]> getCumulativeCountsBeforeDate(@Param("beforeDate") Date beforeDate);
}
