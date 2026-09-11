package com.pcpl.carbon.pcplsdk.Kitkat.KkRacePlayed.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kk_race_played")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KkRacePlayed extends ApplicationModel {

    @Column(name = "game_id")
    private String gameId;

    @Column(name = "console_id")
    private Integer consoleId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "player_name")
    private String playerName;

    @Column(name="country")
    private String country;

    @Column(name="car_name")
    private String carName;

    @Column(name="track_id")
    private Integer trackId;

    @Column(name="result")
    private Integer result;

    @Column(name="race_duration")
    private Float raceDuration;

    @Column(name="lap_duration")
    private Float lapDuration;

    @Column(name="brand_count")
    private Integer brandCount;

    @Column(name="race_info")
    private String raceInfo;

}
