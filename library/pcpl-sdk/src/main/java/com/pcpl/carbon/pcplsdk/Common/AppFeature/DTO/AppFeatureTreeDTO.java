package com.pcpl.carbon.pcplsdk.Common.AppFeature.DTO;

import com.pcpl.carbon.pcplsdk.Common.AppFeature.Model.AppFeature;
import lombok.Data;

import java.util.List;

@Data
public class AppFeatureTreeDTO {
    private Long id;
    private Long tenantId;
    private String featureName;
    private String text;
    private String toolTip;
    private String systemRole;
    private String url;
    private Integer sequence;
    private Integer isSystem;
    private List<AppFeatureTreeDTO> children;

    public AppFeatureTreeDTO() {
    }

    public AppFeatureTreeDTO(AppFeature appFeature) {
        this.id = appFeature.getId();
        this.featureName = appFeature.getFeatureName();
        this.text = appFeature.getFeatureName();
        this.toolTip = appFeature.getToolTip();
        this.systemRole = appFeature.getSystemRole();
        this.url = appFeature.getUrl();
        this.sequence = appFeature.getSequence();
        this.isSystem = appFeature.getIsSystem();
    }
}


