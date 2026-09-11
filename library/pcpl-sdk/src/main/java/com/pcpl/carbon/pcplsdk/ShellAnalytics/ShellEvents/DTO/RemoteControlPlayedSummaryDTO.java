package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoteControlPlayedSummaryDTO {
    private String country;
    private String carName;
    private Double totalRcDuration;
    private Double cumulativeRcDuration;
    private String totalRacDurationInHours;
    private String cumulativeRacDurationInHours;
}
