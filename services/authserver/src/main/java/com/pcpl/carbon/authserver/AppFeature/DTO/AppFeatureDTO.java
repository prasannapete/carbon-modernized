package com.pcpl.carbon.authserver.AppFeature.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Transfer object for AppFeature CRUD in the authserver.
 *
 * <p>Parent/child is represented flatly to avoid infinite JSON recursion: {@code parentId} is
 * the FK to the parent feature, {@code parent} is an optional shallow parent DTO (its own
 * {@code parent}/{@code childrens} are left null), and {@code childrens} is the (optionally
 * nested) list of child DTOs. When a tree is built, children carry only {@code parentId}, never
 * a back-reference to {@code parent}, so the graph never cycles.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppFeatureDTO {

    private Long id;
    private Long tenantId;
    private String featureName;
    private String toolTip;
    private String systemRole;
    private String url;
    private Integer sequence;
    private Integer isSystem;

    private Long parentId;
    private AppFeatureDTO parent;
    private List<AppFeatureDTO> childrens;

    private Long createdBy;
    private Date creationTime;
    private Long lastModifiedBy;
    private Date lastModifiedTime;
    private Integer isDeleted;
    private Long deletedBy;
    private Date deletedTime;
}
