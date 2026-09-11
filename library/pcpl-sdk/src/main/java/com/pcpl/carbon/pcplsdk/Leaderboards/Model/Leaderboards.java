package com.pcpl.carbon.pcplsdk.Leaderboards.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "srl_leaderboards")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Leaderboards extends ApplicationModel {
    @Column(name = "leaderboard_name")
    private String leaderboardName;

    @Column(name = "leaderboard_internal_name")
    private String leaderboardInternalName;

    @Column(name = "leaderboard_id")
    private String leaderboardId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "is_active")
    private int isActive;
}
