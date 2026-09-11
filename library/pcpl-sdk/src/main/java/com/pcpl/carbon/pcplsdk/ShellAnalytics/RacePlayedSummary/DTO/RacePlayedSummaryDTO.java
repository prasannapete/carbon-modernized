package com.pcpl.carbon.pcplsdk.ShellAnalytics.RacePlayedSummary.DTO;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RacePlayedSummaryDTO extends ApplicationModel {
    private Long tenantId;
    private String carName;
    private String country;
    private Date generatedDate;
    private float raceDuration;
    private String cumulativeRaceDuration;


}
