package com.pcpl.carbon.pcplsdk.Kitkat.KkBrandViewedGarage.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KkBrandViewedGarageDTO {
    private Long id;
    private String gameId;
    private Integer consoleId;
    private Long tenantId;
    private String playerName;
    private String country;
    private Integer brandCount;
    private Long createdBy;
    private Date creationTime;
    private Long lastModifiedBy;
    private Date lastModifiedTime;
    private int isDeleted;
    private Long deletedBy;
    private Date deletedTime;
}
