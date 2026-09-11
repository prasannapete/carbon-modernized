package com.pcpl.carbon.pcplsdk.PlayMobil.Players.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "cb_player_feature_mapping")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerFeatureMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "feature_type_id", length = 50)
    private String featureTypeId;

    @Column(name = "feature_type_name")
    private String featureTypeName;

    @Column(name = "feature_code", length = 50)
    private String featureCode;

    @Column(name = "feature_name")
    private String featureName;

    @Column(name = "color_code", length = 50)
    private String colorCode;

    @Column(name = "color_name")
    private String colorName;

    @Column(name = "creation_time")
    private Date creationTime;

    @Column(name = "last_modified_time")
    private Date lastModifiedTime;

    @Column(name = "is_deleted")
    private int isDeleted;
}
