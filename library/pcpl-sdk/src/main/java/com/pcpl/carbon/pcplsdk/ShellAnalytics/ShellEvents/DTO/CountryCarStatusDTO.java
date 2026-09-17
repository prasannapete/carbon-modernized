package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryCarStatusDTO {
    private Long id;
    private Long tenantId;
    private String country;
    private String carName;
    private Integer isActive;
}
