package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandViewedGarageDTO{
    private Long id;
    private Long tenantId;
    private int brandCount;
    private String gameId;
    private String country;
}
