package com.pcpl.carbon.pcplsdk.Kitkat.KkBrandViewedGarage.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kk_brand_viewed_garage")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KkBrandViewedGarage extends ApplicationModel {

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

    @Column(name="brand_count")
    private Integer brandCount;

}
