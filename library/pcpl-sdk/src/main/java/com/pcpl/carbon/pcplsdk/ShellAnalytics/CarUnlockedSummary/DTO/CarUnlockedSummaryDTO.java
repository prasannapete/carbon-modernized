package com.pcpl.carbon.pcplsdk.ShellAnalytics.CarUnlockedSummary.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarUnlockedSummaryDTO {
    private Long id;
    private Long tenantId;
    private String carName;
    private String country;
    private Date generatedDate;
    private long unlockedCount;
    private long cumulativeUnlockedCount;
    private Long createdBy;
    private Date creationTime;
    private Long lastModifiedBy;
    private Date lastModifiedTime;
    private Integer isDeleted;
    private Long deletedBy;
    private Date deletedTime;
}
