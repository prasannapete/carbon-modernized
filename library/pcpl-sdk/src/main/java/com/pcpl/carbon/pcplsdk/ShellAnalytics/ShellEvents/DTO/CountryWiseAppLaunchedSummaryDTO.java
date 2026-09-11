package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryWiseAppLaunchedSummaryDTO {
    private String country;
    private Long launches;
    private Long cumulative;
    private String countryName;
}
