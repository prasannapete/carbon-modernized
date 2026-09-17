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
@Table(name = "srl_offer_issue_details")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class OfferIssueDetails extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "consumer_uuid")
    private String consumerUuid;

    @Column(name = "event_type")
    private Integer eventType;

    @Column(name = "offer_code")
    private String offerCode;

    @Column(name = "issued_date")
    private Date issuedDate;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "game_id")
    private String gameId;
}
