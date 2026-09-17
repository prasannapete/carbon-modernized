package com.pcpl.carbon.pcplsdk.ShellAnalytics.AppLaunchedSummary.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppLaunchedSummaryDTO {

    private Long id;
    private Long tenantId;
    private String country;
    private Date generatedDate;
    private long appLaunchedCount;
    private long cumulativeAppLaunchedCount;
    private Long createdBy;
    private Date creationTime;
    private Long lastModifiedBy;
    private Date lastModifiedTime;
    private Integer isDeleted;
    private Long deletedBy;
    private Date deletedTime;
}
