package com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "srl_user_countries")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCountries extends ApplicationModel {

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "country_id")
    private Long countryId;
}
