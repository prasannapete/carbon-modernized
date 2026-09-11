package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryWiseBrandViewsSummaryDTO {
    private String country;
    private Long views;
    private Long cumulative;
    private String countryName;
}
