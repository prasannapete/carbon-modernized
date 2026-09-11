package com.pcpl.carbon.pcplsdk.Common.AppFeature.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pcpl.carbon.pcplsdk.Common.AppFeature.Model.AppFeature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppFeatureDTO {
    private Long id;
    private String mibId;
    private Long parentId;
    private Long tenantId;
    private String featureName;
    private String toolTip;
    private String systemRole;
    private String url;
    private Integer sequence;
    private Integer isSystem;
    private Long createdBy;
    private Date creationTime;
    private Long lastModifiedBy;
    private Date lastModifiedTime;
    private int isDeleted;
    private Long deletedBy;
    private Date deletedTime;
    @ToString.Exclude
    private AppFeatureDTO parent;
    @ToString.Exclude
    private List<AppFeatureDTO> childrens = new ArrayList<>();

    public AppFeatureDTO(AppFeature appFeature) {
        this.id = appFeature.getId();
        this.mibId = appFeature.getMibId();
        this.featureName = appFeature.getFeatureName();
        this.toolTip = appFeature.getToolTip();
        this.systemRole = appFeature.getSystemRole();
        this.url = appFeature.getUrl();
        this.sequence = appFeature.getSequence();
        this.isSystem = appFeature.getIsSystem();
        this.createdBy = appFeature.getCreatedBy();
        this.creationTime = appFeature.getCreationTime();
        this.lastModifiedBy = appFeature.getLastModifiedBy();
        this.lastModifiedTime = appFeature.getLastModifiedTime();
        this.isDeleted = appFeature.getIsDeleted();
        this.deletedBy = appFeature.getDeletedBy();
        this.deletedTime = appFeature.getDeletedTime();
        this.childrens = new ArrayList<>();
        if (appFeature.getParent() != null)
            this.setParent(AppFeatureDTO.getSimple(appFeature.getParent()));
        appFeature.getChildrens().forEach(appFeature1 -> {
            this.childrens.add(new AppFeatureDTO(appFeature1));
        });
    }

    private static AppFeatureDTO getSimple(AppFeature appFeature) {
        AppFeatureDTO appFeatureDTO = new AppFeatureDTO();
        appFeatureDTO.setId(appFeature.getId());
        appFeatureDTO.setFeatureName(appFeature.getFeatureName());
        appFeatureDTO.setToolTip(appFeature.getToolTip());
        appFeatureDTO.setSystemRole(appFeature.getSystemRole());
        appFeatureDTO.setUrl(appFeature.getUrl());
        appFeatureDTO.setSequence(appFeature.getSequence());
        appFeatureDTO.setIsSystem(appFeature.getIsSystem());
        appFeatureDTO.setCreatedBy(appFeature.getCreatedBy());
        appFeatureDTO.setCreationTime(appFeature.getCreationTime());
        appFeatureDTO.setLastModifiedBy(appFeature.getLastModifiedBy());
        appFeatureDTO.setLastModifiedTime(appFeature.getLastModifiedTime());
        appFeatureDTO.setIsDeleted(appFeature.getIsDeleted());
        appFeatureDTO.setDeletedBy(appFeature.getDeletedBy());
        appFeatureDTO.setDeletedTime(appFeature.getDeletedTime());
        return appFeatureDTO;
    }
}
