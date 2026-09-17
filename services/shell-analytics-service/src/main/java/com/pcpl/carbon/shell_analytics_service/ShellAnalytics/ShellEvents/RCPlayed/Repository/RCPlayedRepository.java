package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RCPlayed.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.RemoteControlPlayedGraphDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.RCPlayed;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface RCPlayedRepository extends PCPLCRUDRepository<RCPlayed> {
    @Query(value = """
    SELECT
            r.country,r.car_name,
                                         ROUND(sum(r.rc_duration)/60),
                                         ROUND(ROUND(sum(r.rc_duration) / 60) / 60)                               as race_hours,
                                   (ROUND(sum(r.rc_duration) / 60) / 60)::int % 60                          as remining_minutes,
                                   concat(ROUND(ROUND(sum(r.rc_duration) / 60) / 60):: text, ' h ',
                                          ((ROUND(sum(r.rc_duration) / 60) / 60)::int % 60):: text, ' min') as data,
                                        country.country_name
                                    FROM srl_rc_played r
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
    SELECT new com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.RemoteControlPlayedGraphDTO(
        r.country,
        r.carName,
         ROUND(sum(r.rcDuration)/60),
        0.0,
        country.countryName,
        1,
        CONCAT(
                    ROUND(ROUND(SUM(r.rcDuration) / 60) / 60),
                    ' h ',
                    MOD(CAST(ROUND(ROUND(SUM(r.rcDuration) / 60) / 60) AS integer), 60),
                    ' min'
                )
    )
    FROM RCPlayed r
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
    List<RemoteControlPlayedGraphDTO> getRemoteControlPlayedStatsByDateRange(@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("countries") List<String> countries);


    @Query("SELECT DISTINCT r.country FROM RCPlayed r WHERE r.isDeleted = 0 AND r.country IS NOT NULL")
    List<String> getCountryByRCPlayed();

    @Query("SELECT DISTINCT r.carName FROM RCPlayed r WHERE r.country = :country " +
            "AND CAST(r.creationTime AS LocalDate) = :date AND r.isDeleted = 0")
    List<String> getCarNamesByCountryAndDate(@Param("country") String country, @Param("date") Date date);

    @Query("SELECT COALESCE(SUM(r.rcDuration), 0) FROM RCPlayed r " +
            "WHERE r.country = :country AND r.carName = :carName " +
            "AND CAST(r.creationTime AS LocalDate) = :date AND r.isDeleted = 0")
    Float getDurationByCountryCarAndDate(@Param("country") String country, @Param("carName") String carName, @Param("date") Date date);

    @Query("SELECT COALESCE(SUM(r.rcDuration), 0) FROM RCPlayed r " +
            "WHERE r.country = :country AND r.carName = :carName " +
            "AND CAST(r.creationTime AS LocalDate) <= :date AND r.isDeleted = 0")
    Float getCumulativeDurationByCountryCarUpToDate(@Param("country") String country, @Param("carName") String carName, @Param("date") Date date);


    @Query("""
        SELECT
            rp.country,
            rp.carName,
            CAST(rp.creationTime AS date) AS generatedDate,
            SUM(rp.rcDuration) AS dailyDuration
        FROM RCPlayed rp
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
    List<Object[]> getDailyRCDuration(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("""
        SELECT
            rp.country,
            rp.carName,
            SUM(rp.rcDuration) AS cumulativeDuration
        FROM RCPlayed rp
        WHERE rp.isDeleted = 0
          AND rp.creationTime < :beforeDate
        GROUP BY
            rp.country,
            rp.carName
    """)
    List<Object[]> getCumulativeRCDurationBeforeDate(
            @Param("beforeDate") Date beforeDate
    );
}
