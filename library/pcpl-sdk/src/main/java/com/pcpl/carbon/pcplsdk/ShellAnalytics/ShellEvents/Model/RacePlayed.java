package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "srl_race_played")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RacePlayed extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "car_name")
    private String carName;

    @Column(name = "track_id")
    private int trackId;

    @Column(name = "result")
    private int result;

    @Column(name = "is_competition")
    private boolean isCompetition;

    @Column(name = "race_duration")
    private float raceDuration;

    @Column(name = "brand_count")
    private int brandCount;

    @Column(name = "game_id")
    private String gameId;

    @Column(name = "country")
    private String country;

    @Column(name ="lap_duration")
    private float lapDuration;

    @Column(name ="race_info")
    private String raceInfo;
}
