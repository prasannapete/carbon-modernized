package com.pcpl.carbon.pcplsdk.ClientDetails.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "srl_client_details")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDetails extends ApplicationModel {

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "market")
    private String market;
}
