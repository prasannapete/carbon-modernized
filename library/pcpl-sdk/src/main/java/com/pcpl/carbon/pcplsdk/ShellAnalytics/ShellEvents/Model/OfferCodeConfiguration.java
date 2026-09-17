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

import java.util.Date;

@Entity
@Table(name = "srl_offer_code_configuration")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class OfferCodeConfiguration extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "county")
    private String country;

    @Column(name = "event_type")
    private Integer eventType;

    @Column(name = "offer_code")
    private String offerCode;

    @Column(name = "valid_from")
    private Date validFrom;

    @Column(name = "valid_until")
    private Date validUntil;

    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "points")
    private Integer points;

    @Column(name = "validity_period")
    private Integer validityPeriod;
}
