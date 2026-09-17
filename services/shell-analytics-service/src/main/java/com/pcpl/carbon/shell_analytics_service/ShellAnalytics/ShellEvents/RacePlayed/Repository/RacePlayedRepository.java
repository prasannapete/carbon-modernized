package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RacePlayed.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.RacePlayedGraphDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.RacePlayed;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface RacePlayedRepository extends PCPLCRUDRepository<RacePlayed> {
    @Query(value = """
    SELECT
                r.country,r.car_name,
                                             ROUND(sum(r.race_duration)/60),
                                             ROUND(ROUND(sum(r.race_duration) / 60) / 60)                               as race_hours,
                                       (ROUND(sum(r.race_duration) / 60) / 60)::int % 60                          as remining_minutes,
                                       concat(ROUND(ROUND(sum(r.race_duration) / 60) / 60):: text, ' h ',
                                              ((ROUND(sum(r.race_duration) / 60) / 60)::int % 60):: text, ' min') as data,
                                            country.country_name
                                        FROM srl_race_played r
                                        left JOIN srl_country country ON r.country = country.country and country.is_deleted=0
                                         WHERE
                                            r.is_deleted = 0
                                        And r.country IN (:countries) And
                                          r.car_name IN (
                                              SELECT mc.car_name FROM srl_master_cars mc
                                              WHERE (mc.country_code IN (:countries)) and mc.is_active=1)
                                                AND r.game_id IN (SELECT DISTINCT (cu.game_id) as gameId
                                                                FROM srl_car_unlocked cu
                                                                Where cu.car_name IN (SELECT mc2.car_name FROM srl_master_cars mc2 WHERE mc2.country_code IN (:countries) and mc2.is_active=1)
                                                                  AND (cu.country IN :countries) )
                                          GROUP BY r.car_name, r.country,country.country_name
                                          ORDER BY r.country
""",nativeQuery = true)
    List<Object[]> getTotalCumulativeCounts(@Param("countries") List<String> countries);


    @Query("""
    SELECT new com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.RacePlayedGraphDTO(
        r.country,
        r.carName,
         ROUND(sum(r.raceDuration)/60),
        0.0,
        country.countryName,
        1,
        CONCAT(
                    ROUND(ROUND(SUM(r.raceDuration) / 60) / 60),
                    ' h ',
                    MOD(CAST(ROUND(ROUND(SUM(r.raceDuration) / 60) / 60) AS integer), 60),
                    ' min'
                )
    )
    FROM RacePlayed r
    left JOIN Country country ON r.country = country.country and country.isDeleted=0
     where
             r.isDeleted=0
             and cast(r.creationTime as date)>=:startDate
             and cast(r.creationTime as date) >= (SELECT cast(min(cu.creationTime) as date)
                                                            FROM CarUnlocked cu
                                                            Where cu.carName IN (SELECT mc2.carName
                                                                                  FROM MasterCars mc2
                                                                                  WHERE mc2.countryCode IN ((:countries))
                                                                                    and mc2.isActive = 1
                                                                                    AND mc2.isDeleted = 0)
                                                              And cu.gameId = r.gameId
                                                              and cu.isDeleted = 0
                                                              AND (cu.country IN (:countries)))
             and cast(r.creationTime as date)<=:endDate
             and r.country in (:countries)
             and r.carName in (select
                 mc1_0.carName
             from
                 MasterCars mc1_0
             where
                 (mc1_0.countryCode in (:countries))
                 and mc1_0.isActive=1)
             and r.gameId in (select
                 distinct carUnlocked.gameId
             from
                 CarUnlocked carUnlocked
             where
                 carUnlocked.carName in (select
                     mc2.carName
                 from
                     MasterCars mc2
                 where
                     mc2.countryCode in (:countries)
                     and mc2.isActive=1)
                 and (carUnlocked.country in (:countries)))
         group by
             r.carName,
             r.country,
             country.countryName
         order by
             r.country
""")
    List<RacePlayedGraphDTO> getRemoteControlPlayedStatsByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("countries") List<String> countries);

    @Query("SELECT r.country, r.carName, ROUND(sum(r.raceDuration)/60),country.countryName " +
            "FROM RacePlayed r " +
            "LEFT JOIN Country country ON r.country = country.country and country.isDeleted=0 "+
            "WHERE r.isDeleted = 0 " +
            "GROUP BY r.country, r.carName,country.countryName" +
            " ORDER BY country.countryName")
    List<Object[]> getTotalLifetimeCumulative();

    @Query("""
    SELECT r.country, country.countryName, COUNT(r.gameId), Count(DISTINCT r.gameId)
    FROM RacePlayed r
    LEFT JOIN Country country ON country.country = r.country AND country.isDeleted = 0
    WHERE 
        r.isCompetition = true
        AND r.isDeleted = 0
        AND CAST(r.creationTime as Date) >= :startDate and CAST(r.creationTime as date) <= :endDate
        AND (:countries IS NULL OR r.country IN :countries)
    GROUP BY r.country, country.countryName
    ORDER BY country.countryName
""")
    List<Object[]> getCompetitionStatsByDateRange(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("countries") List<String> countries
    );


    @Query("SELECT DISTINCT r.country FROM RacePlayed r WHERE r.isDeleted = 0 AND r.country IS NOT NULL")
    List<String> getCountryByRacePlayed();

    @Query("SELECT DISTINCT r.carName FROM RacePlayed r WHERE r.country = :country " +
            "AND CAST(r.creationTime AS LocalDate) = :date AND r.isDeleted = 0")
    List<String> getCarNamesByCountryAndDate(@Param("country") String country,
                                             @Param("date") Date date);

    @Query("SELECT COALESCE(SUM(r.raceDuration), 0) FROM RacePlayed r " +
            "WHERE r.country = :country AND r.carName = :carName " +
            "AND CAST(r.creationTime AS LocalDate) = :date AND r.isDeleted = 0")
    Float getDurationByCountryCarAndDate(@Param("country") String country,
                                         @Param("carName") String carName,
                                         @Param("date") Date date);

    @Query("SELECT COALESCE(SUM(r.raceDuration), 0) FROM RacePlayed r " +
            "WHERE r.country = :country AND r.carName = :carName " +
            "AND CAST(r.creationTime AS LocalDate) <= :date AND r.isDeleted = 0")
    Float getCumulativeDurationByCountryCarUpToDate(@Param("country") String country,
                                                    @Param("carName") String carName,
                                                    @Param("date") Date date);

    @Query("""
        SELECT
            rp.country,
            rp.carName,
            CAST(rp.creationTime AS date) AS raceDate,
            SUM(rp.raceDuration) AS dailyDuration
        FROM RacePlayed rp
        WHERE rp.isDeleted = 0
          AND rp.creationTime >= :startDate
          AND rp.creationTime <= :endDate
        GROUP BY
            CAST(rp.creationTime AS date),
            rp.country,
            rp.carName
        ORDER BY
            CAST(rp.creationTime AS date),
            rp.country,
            rp.carName
    """)
    List<Object[]> getDailyRaceDuration(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("""
        SELECT
            rp.country,
            rp.carName,
            SUM(rp.raceDuration) AS cumulativeDuration
        FROM RacePlayed rp
        WHERE rp.isDeleted = 0
          AND rp.creationTime < :beforeDate
        GROUP BY
            rp.country,
            rp.carName
    """)
    List<Object[]> getCumulativeDurationBeforeDate(
            @Param("beforeDate") Date beforeDate
    );
}
