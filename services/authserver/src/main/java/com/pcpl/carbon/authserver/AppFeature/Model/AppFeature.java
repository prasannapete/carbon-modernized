package com.pcpl.carbon.authserver.AppFeature.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cb_app_features")
@Getter
@Setter
@ToString
@Audited
@NoArgsConstructor
public class AppFeature {
    @Id
    @Column(name = "id")
    @Length(max = 40)
    @GeneratedValue
    private Long id;

    @Column(name = "feature_name")
    @Length(max = 255)
    private String featureName;

    @Column(name = "tooltip")
    @Length(max = 255)
    private String toolTip;

    @Column(name = "system_role")
    @Length(max = 1000)
    private String systemRole;

    @Column(name = "url")
    @Length(max = 1000)
    private String url;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "is_system")
    private Integer isSystem;

    @Column(name = "created_by")
    @Length(max = 40)
    private Long createdBy;

    @Column(name = "creation_time")
    private Date creationTime;

    @Column(name = "last_modified_by")
    @Length(max = 40)
    private Long lastModifiedBy;

    @Column(name = "last_Modified_time")
    private Date lastModifiedTime;

    @Column(name = "is_deleted")
    private  int isDeleted;

    @Column(name = "deleted_by")
    @Length(max = 40)
    private Long deletedBy;

    @Column(name = "deleted_time")
    private Date deletedTime;

    @ManyToOne(cascade={CascadeType.ALL})
    @JoinColumn(name="parent_id")
    @JsonBackReference
    private AppFeature parent;

    @OneToMany(mappedBy="parent")
    @JsonManagedReference
    private List<AppFeature> childrens = new ArrayList<>();

    public AppFeature(Long id, String featureName, String toolTip, String systemRole, String url, Integer sequence, Integer isSystem, Long createdBy, Date creationTime, Long lastModifiedBy, Date lastModifiedTime, int isDeleted, Long deletedBy, Date deletedTime) {
        this.id = id;
        this.featureName = featureName;
        this.toolTip = toolTip;
        this.systemRole = systemRole;
        this.url = url;
        this.sequence = sequence;
        this.isSystem = isSystem;
        this.createdBy = createdBy;
        this.creationTime = creationTime;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedTime = lastModifiedTime;
        this.isDeleted = isDeleted;
        this.deletedBy = deletedBy;
        this.deletedTime = deletedTime;
    }
}
