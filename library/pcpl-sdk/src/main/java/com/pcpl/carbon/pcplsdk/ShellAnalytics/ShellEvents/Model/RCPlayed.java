package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import org.hibernate.annotations.Filter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "srl_rc_played")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class RCPlayed extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "car_name")
    private String carName;

    @Column(name = "rc_duration")
    private float rcDuration;

    @Column(name = "game_id")
    private String gameId;

    @Column(name = "country")
    private String country;
}
