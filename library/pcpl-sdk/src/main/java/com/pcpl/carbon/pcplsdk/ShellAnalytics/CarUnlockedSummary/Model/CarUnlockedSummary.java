package com.pcpl.carbon.pcplsdk.ShellAnalytics.CarUnlockedSummary.Model;

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
@Table(name = "srl_car_unlocked_summary")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class CarUnlockedSummary extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "car_name")
    private String carName;

    @Column(name = "country")
    private String country;

    @Column(name = "generated_date")
    private Date generatedDate;

    @Column(name = "unlocked_count")
    private long unlockedCount;

    @Column(name="cumulative_unlocked_count")
    private long cumulativeUnlockedCount;
}
