package com.pcpl.carbon.pcplsdk.Common.AppFeature.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cb_app_features")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppFeature extends ApplicationModel {

    @Column(name = "mib_id")
    private String mibId;

    @Column(name = "tenant_id")
    private Long tenantId;

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

    @ManyToOne(cascade={CascadeType.ALL})
    @JoinColumn(name="parent_id")
    @JsonBackReference
    private AppFeature parent;

    @OneToMany(mappedBy="parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<AppFeature> childrens = new ArrayList<>();
}
