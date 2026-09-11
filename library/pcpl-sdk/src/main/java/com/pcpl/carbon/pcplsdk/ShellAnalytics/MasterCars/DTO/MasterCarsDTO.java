package com.pcpl.carbon.pcplsdk.ShellAnalytics.MasterCars.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MasterCarsDTO {
    private long id;
    private Long tenantId;
    private String country;
    private String countryCode;
    private String carName;
    private int isActive;
}
