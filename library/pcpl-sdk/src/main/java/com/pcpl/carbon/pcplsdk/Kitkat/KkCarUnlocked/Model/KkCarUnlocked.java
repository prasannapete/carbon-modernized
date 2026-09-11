package com.pcpl.carbon.pcplsdk.Kitkat.KkCarUnlocked.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kk_car_unlocked")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KkCarUnlocked extends ApplicationModel {
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


}
