package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarUnlockedDTO {
    private Long id;
    private Long tenantId;
    private String carName;
    private int unlockMethod;
    private String gameId;
    private String country;
    private Integer isActive;
    private String countryName;
}
