package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoyaltyOauthCredentialsDTO {
    private Long id;
    private Long tenantId;
    private String country;
    private String clientId;
    private String clientSecret;
    private Long createdBy;
    private Date createdDate;
    private Long lastModifiedBy;
    private Date lastModifiedDate;
    private Integer isDeleted;
    private Long deletedBy;
    private Date deletedDate;
}
