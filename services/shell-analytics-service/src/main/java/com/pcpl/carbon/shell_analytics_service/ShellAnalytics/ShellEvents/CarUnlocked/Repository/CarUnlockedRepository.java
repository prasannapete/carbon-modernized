package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CarUnlocked.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CarUnlockedDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CarUnlockedGraphDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.CarUnlocked;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface CarUnlockedRepository extends PCPLCRUDRepository<CarUnlocked> {
    @Query("SELECT new com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CarUnlockedGraphDTO(" +
            "c.country, c.carName, COUNT(c), 0L, country.countryName, " +
            "COALESCE(cs.isActive, 0)) " +
            "FROM CarUnlocked c " +
            "LEFT JOIN MasterCars cs ON cs.carName = c.carName AND cs.countryCode = c.country AND cs.isActive=1 " +
            "LEFT JOIN Country country ON c.country = country.country AND country.isDeleted = 0 " +
            "WHERE CAST(c.creationTime as Date) >= :startDate and CAST(c.creationTime as date) <= :endDate " +
            "AND c.isDeleted = 0 " +
            "AND (:countries IS NULL OR c.country IN :countries) " +
            "AND (c.country IN (SELECT DISTINCT m.countryCode FROM MasterCars m) " +
            "     AND cs.id IS NOT NULL " +
            "     OR c.country NOT IN (SELECT DISTINCT m2.countryCode FROM MasterCars m2)) " +
            "GROUP BY c.country, c.carName, country.countryName, cs.isActive " +
            "ORDER BY country.countryName")
    List<CarUnlockedGraphDTO> getCarUnlockStatsByDateRange(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("countries") List<String> countries
    );

    @Query("SELECT c.country, c.carName, COUNT(c) FROM" +
            " CarUnlocked c" +
            " Where c.carName In(SELECT DISTINCT m.carName FROM MasterCars m where m.isActive=1)" +
            " GROUP BY c.country, c.carName")
    List<Object[]> getTotalCumulativeCounts();


    @Query("SELECT DISTINCT new com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CarUnlockedDTO(" +
            "MIN(c.id),MIN(c.tenantId),c.carName,MIN(c.unlockMethod), MIN(c.gameId), MIN(c.country),COALESCE(MIN(cs.isActive), 0), country.countryName) " +
            "FROM CarUnlocked c " +
            "LEFT JOIN CountryCarStatus cs " +
            "    ON cs.carName = c.carName AND cs.country = c.country "+
            "LEFT JOIN Country country ON c.country = country.country and country.isDeleted=0 "+
            "WHERE c.country=:country  AND c.isDeleted =:isDeleted GROUP BY c.carName,country.countryName order by country.countryName")
    List<CarUnlockedDTO> findAllByCountryAndIsDeletedDistinctOnCarName(String country, int isDeleted);

    @Query(value = """

            SELECT
                                      uc.country_id AS countryId,
                                      c.country_name AS countryName,
                                      c.country AS countryCode,
                                      COALESCE(u.unique_users, 0) AS totalUsers,
                                      COALESCE(cu.totalCarsUnlocked, 0) AS totalCarsUnlocked,
                                      COALESCE(rc.totalRCPlayed, 0) AS totalRCPlayed,
                                      COALESCE(rp.totalRacePlayed, 0) AS totalRacePlayed,
                                      a.game_id As cummulativeUsers
                                  FROM srl_user_countries uc
                                  LEFT JOIN srl_country c ON c.id = uc.country_id
                                  LEFT JOIN (
                                  SELECT
                                                            country,
                                                            COUNT(DISTINCT game_id) AS unique_users
                                                        FROM srl_car_unlocked cu
                                                        WHERE (:countries IS NULL OR cu.country IN :countries)
                                                          AND CAST(cu.creation_time as Date) >= :startDate and CAST(cu.creation_time as date) <= :endDate
                                                          AND car_name IN (
                                                              SELECT car_name
                                                              FROM srl_master_cars
                                                              WHERE (:countries IS NULL OR srl_master_cars.country_code IN :countries) and is_active=1
                                                          )
                                                        GROUP BY country
                              ) u ON u.country = c.country
                                  LEFT JOIN (
                                      SELECT cu.country, COUNT(*) AS totalCarsUnlocked
                                           FROM srl_car_unlocked cu
                                           WHERE CAST(creation_time as Date) >= :startDate and CAST(creation_time as date) <= :endDate
                                     AND CAST(cu.creation_time as Date) >= :startDate and CAST(cu.creation_time as date) <= :endDate
                                         AND cu.car_name IN
                                             (SELECT srl_master_cars.car_name FROM srl_master_cars WHERE (:countries IS NULL OR srl_master_cars.country_code IN :countries) and is_active=1)
                                           GROUP BY cu.country
                                  ) cu ON cu.country = c.country
                                  LEFT JOIN (
                                      SELECT country, COUNT(*) AS totalRCPlayed
                                      FROM srl_rc_played
                                      WHERE CAST(creation_time as Date) >= :startDate and CAST(creation_time as date) <= :endDate
                                      GROUP BY country
                                  ) rc ON rc.country = c.country
                                  LEFT JOIN (
                                      SELECT country, COUNT(*) AS totalRacePlayed
                                      FROM srl_race_played
                                      WHERE CAST(creation_time as Date) >= :startDate and CAST(creation_time as date) <= :endDate
                                      GROUP BY country
                                  ) rp ON rp.country = c.country
                                  Left Join(
                                     SELECT country, Count(DISTINCT (game_id)) as game_id
                                     FROM srl_car_unlocked cu2
                                     WHERE car_name IN (SELECT car_name FROM srl_master_cars WHERE country_code IN (:countries) and is_active=1)
                                     AND country IN (:countries) GROUP BY country
                                 ) a ON a.country = cu.country
                                  WHERE (:countries IS NULL OR c.country IN :countries)
                                  GROUP BY
                                      uc.country_id, c.country_name, c.country,
                                      u.unique_users, cu.totalCarsUnlocked, rc.totalRCPlayed, rp.totalRacePlayed,A.game_id
                        ORDER BY c.country_name
    """, nativeQuery = true)

    List<Object[]> getCountryWiseUserAndEventCounts(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("countries") List<String> countries
    );

    @Query(value = """
       SELECT
         uc.country_id AS countryId,
         c.country_name AS countryName,
         c.country AS countryCode,
         COALESCE(u.unique_users, 0) AS totalUsers,
         COALESCE(cu.totalCarsUnlocked, 0) AS totalCarsUnlocked,
         COALESCE(rc.totalRCPlayed, 0) AS totalRCPlayed,
         COALESCE(rp.totalRacePlayed, 0) AS totalRacePlayed,
         A.game_id As cummulativeUsers
     FROM srl_user_countries uc
     LEFT JOIN srl_country c ON c.id = uc.country_id
     LEFT JOIN (
         SELECT
             country,
             COUNT(DISTINCT game_id) AS unique_users
         FROM srl_car_unlocked cu
         WHERE cu.creation_time BETWEEN :startDate AND :endDate
           AND car_name IN (
               SELECT car_name
               FROM srl_master_cars
           )
         GROUP BY country
     ) u ON u.country = c.country
     LEFT JOIN (
         SELECT cu.country, COUNT(*) AS totalCarsUnlocked
                               FROM srl_car_unlocked cu
                               WHERE creation_time BETWEEN :startDate AND :endDate
                         AND cu.creation_time BETWEEN :startDate AND :endDate
                             AND cu.car_name IN
                                 (SELECT srl_master_cars.car_name FROM srl_master_cars and is_active=1)
                               GROUP BY cu.country
     ) cu ON cu.country = c.country
     LEFT JOIN (
         SELECT
             country,
             COUNT(*) AS totalRCPlayed
         FROM srl_rc_played
         WHERE creation_time BETWEEN :startDate AND :endDate
         GROUP BY country
     ) rc ON rc.country = c.country
     LEFT JOIN (
         SELECT
             country,
             COUNT(*) AS totalRacePlayed
         FROM srl_race_played
         WHERE creation_time BETWEEN :startDate AND :endDate
         GROUP BY country
     ) rp ON rp.country = c.country
     Left Join(
                               SELECT country, Count(DISTINCT (game_id)) as game_id
                               FROM srl_car_unlocked cu2
                               WHERE car_name IN (SELECT car_name FROM srl_master_cars and is_active=1 )
                                GROUP BY country
                           ) A ON A.country = cu.country
     GROUP BY
         uc.country_id,
         c.country_name,
         c.country,
         u.unique_users,
         cu.totalCarsUnlocked,
         rc.totalRCPlayed,
         rp.totalRacePlayed,
         A.game_id
       Order by countryName
    """, nativeQuery = true)
    List<Object[]> getUserAndEventCounts(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );


    @Query("SELECT DISTINCT c.country FROM CarUnlocked c WHERE c.isDeleted = 0 AND c.country IS NOT NULL")
    List<String> getCountryByCarUnlocked();

    @Query("SELECT DISTINCT c.carName FROM CarUnlocked c WHERE c.country = :country " +
            "AND CAST(c.creationTime AS LocalDate) = :date AND c.isDeleted = 0")
    List<String> getCarNamesByCountryAndDate(@Param("country") String country, @Param("date") Date date);

    @Query("SELECT COUNT(c) FROM CarUnlocked c WHERE c.country = :country AND c.carName = :carName " +
            "AND CAST(c.creationTime AS LocalDate) = :date AND c.isDeleted = 0")
    Long getCountByCountryCarAndDate(@Param("country") String country, @Param("carName") String carName, @Param("date") Date date);

    @Query("SELECT COUNT(c) FROM CarUnlocked c WHERE c.country = :country AND c.carName = :carName " +
            "AND CAST(c.creationTime AS LocalDate) <= :date AND c.isDeleted = 0")
    Long getCumulativeCountByCountryCarUpToDate(@Param("country") String country, @Param("carName") String carName, @Param("date") Date date);

    @Query("""
        SELECT cu.country,
               cu.carName,
               CAST(cu.creationTime AS date) AS unlockedDate,
               COUNT(cu.gameId) AS dailyCount
        FROM CarUnlocked cu
        WHERE cu.isDeleted = 0
          AND cu.creationTime >= :startDate
          AND cu.creationTime <= :endDate
        GROUP BY CAST(cu.creationTime AS date), cu.country, cu.carName
        ORDER BY CAST(cu.creationTime AS date), cu.country, cu.carName
    """)
    List<Object[]> getDailyCounts(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("""
        SELECT cu.country,
               cu.carName,
               COUNT(cu.gameId) AS cumulativeCount
        FROM CarUnlocked cu
        WHERE cu.isDeleted = 0
          AND cu.creationTime < :beforeDate
        GROUP BY cu.country, cu.carName
    """)
    List<Object[]> getCumulativeCountsBeforeDate(@Param("beforeDate") Date beforeDate);
}
