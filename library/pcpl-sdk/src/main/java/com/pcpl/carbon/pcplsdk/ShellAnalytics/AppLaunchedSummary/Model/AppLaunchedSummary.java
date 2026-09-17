package com.pcpl.carbon.pcplsdk.ShellAnalytics.AppLaunchedSummary.Model;

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
@Table(name = "srl_app_launched_summary")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class AppLaunchedSummary extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "country")
    private String country;

    @Column(name = "generated_date")
    private Date generatedDate;

    @Column(name = "app_launched_count")
    private long appLaunchedCount;

    @Column(name="cumulative_app_launched_count")
    private long cumulativeAppLaunchedCount;
}
