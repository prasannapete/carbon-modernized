package com.pcpl.carbon.pcplsdk.ShellAnalytics.RCPlayedSummary.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import org.hibernate.annotations.Filter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "srl_rc_played_summary")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class RCPlayedSummary extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "car_name")
    private String carName;

    @Column(name = "country")
    private String country;

    @Column(name = "generated_date")
    private Date generatedDate;

    @Column(name = "rc_duration")
    private float rcDuration;

    @Column(name="cumulative_rc_duration")
    private String cumulativeRCDuration;
}
