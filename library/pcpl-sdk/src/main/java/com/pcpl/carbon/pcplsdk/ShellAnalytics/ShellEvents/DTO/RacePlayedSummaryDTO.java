package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RacePlayedSummaryDTO {
    private String country;
    private String carName;
    private Double totalRcDuration;
    private Double cumulativeRcDuration;
}
