package com.pcpl.carbon.pcplsdk.ShellAnalytics.BrandViewedGarageSummary.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandViewedGarageSummaryDTO {
    private Long id;
    private Long tenantId;
    private String country;
    private Date generatedDate;
    private long brandViewedCount;
    private long cumulativeBrandViewedCount;
    private Long createdBy;
    private Date creationTime;
    private Long lastModifiedBy;
    private Date lastModifiedTime;
    private int isDeleted;
    private Long deletedBy;
    private Date deletedTime;
}
