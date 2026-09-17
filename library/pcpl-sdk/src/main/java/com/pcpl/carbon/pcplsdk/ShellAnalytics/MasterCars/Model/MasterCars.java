package com.pcpl.carbon.pcplsdk.ShellAnalytics.MasterCars.Model;

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
@Table(name="srl_master_cars")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class MasterCars extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name="country")
    private String  country;

    @Column(name="country_code")
    private String countryCode;

    @Column(name="car_name")
    private String carName;

    @Column(name="is_active")
    private int isActive;
}
