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
@Table(name = "srl_country_car_status")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryCarStatus extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "country")
    private String country;

    @Column(name = "car_name")
    private String carName;

    @Column(name = "is_active")
    private Integer isActive;
}
