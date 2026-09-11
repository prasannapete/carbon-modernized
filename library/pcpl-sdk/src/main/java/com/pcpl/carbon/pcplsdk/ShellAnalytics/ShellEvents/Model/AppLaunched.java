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
@Table(name = "srl_app_launched")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppLaunched extends ApplicationModel {

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "game_id")
    private String gameId;

    @Column(name = "country")
    private String country;
}
