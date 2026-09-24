package com.pcpl.carbon.pcplsdk.Common.Role.DTO;

import com.pcpl.carbon.pcplsdk.Common.AppFeature.Model.AppFeature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {

    private Long id;
    private String clientId;
    private String name;
    private String homePage;
    private String description;
    private List<AppFeature> appFeatures;
    private String appFeatureIds;
    private String tenantId;

    public RoleDTO(Long id, String clientId, String name, String homePage, String description, List<AppFeature> appFeatures) {
        this.id = id;
        this.clientId = clientId;
        this.name = name;
        this.homePage = homePage;
        this.description = description;
        this.appFeatures = appFeatures;
    }
}
