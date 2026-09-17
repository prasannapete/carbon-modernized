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
@Table(name = "srl_brand_viewed_garage")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class BrandViewedGarage extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "brand_count")
    private int brandCount;

    @Column(name = "game_id")
    private String gameId;

    @Column(name = "country")
    private String country;
}
