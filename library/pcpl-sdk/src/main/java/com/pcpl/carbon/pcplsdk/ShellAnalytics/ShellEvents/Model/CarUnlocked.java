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
@Table(name = "srl_car_unlocked")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarUnlocked extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "car_name")
    private String carName;

    @Column(name = "unlock_method")
    private int unlockMethod;

    @Column(name = "game_id")
    private String gameId;

    @Column(name = "country")
    private String country;
}
