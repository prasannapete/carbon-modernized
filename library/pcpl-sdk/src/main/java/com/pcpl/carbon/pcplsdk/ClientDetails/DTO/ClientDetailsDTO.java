package com.pcpl.carbon.pcplsdk.ClientDetails.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDetailsDTO {
    private Long id;
    private String clientId;
    private Long tenantId;

    private String clientSecret;

    private String countryCode;

    private String market;
}
