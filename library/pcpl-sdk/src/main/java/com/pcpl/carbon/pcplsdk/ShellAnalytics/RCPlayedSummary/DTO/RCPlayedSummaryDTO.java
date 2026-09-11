package com.pcpl.carbon.pcplsdk.ShellAnalytics.RCPlayedSummary.DTO;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RCPlayedSummaryDTO extends ApplicationModel {
    private Long tenantId;
    private String carName;
    private String country;
    private Date generatedDate;
    private float rcDuration;
    private String cumulativeRCDuration;

}
