package com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCountriesDTO {
    private Long Id;
    private Long userId;
    private Long countryId;
    private Long tenantId;
    private Long createdBy;
    private Date createdDate;
    private Long lastModifiedBy;
    private Date lastModifiedDate;
    private Integer isDeleted;
    private Long deletedBy;
    private Date deletedDate;
    private String countryName;
    private String CountryCode;
}
