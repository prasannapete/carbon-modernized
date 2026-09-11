package com.pcpl.carbon.pcplsdk.ShellAnalytics.BrandViewedGarageSummary.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "srl_brand_viewed_garage_summary")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandViewedGarageSummary extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "country")
    private String country;

    @Column(name = "generated_date")
    private Date generatedDate;

    @Column(name = "brand_viewed_count")
    private long brandViewedCount;

    @Column(name="cumulative_brand_viewed_count")
    private long cumulativeBrandViewedCount;
}
