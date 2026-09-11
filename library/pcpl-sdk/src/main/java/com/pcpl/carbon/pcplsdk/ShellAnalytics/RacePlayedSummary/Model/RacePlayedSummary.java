package com.pcpl.carbon.pcplsdk.ShellAnalytics.RacePlayedSummary.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.util.Date;

@Entity
@Table(name = "srl_race_played_summary")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RacePlayedSummary extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "car_name")
    private String carName;

    @Column(name = "country")
    private String country;

    @Column(name = "generated_date")
    private Date generatedDate;

    @Column(name = "race_duration")
    private float raceDuration;

    @Column(name="cumulative_race_duration")
    private String cumulativeRaceDuration;
}
