package com.pcpl.carbon.pcplsdk.PlayMobil.Players.Model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "cb_players")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Players  {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "opta_id",length = 100)
    private String optaId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "shirt_name")
    private String shirtName;

    @Column(name = "number")
    private Integer number;

    @Column(name = "position")
    private int position;

    @Column(name = "team")
    private String team;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "nationality",length = 100)
    private String nationality;

    @Column(name = "home_town")
    private String homeTown;

    @Column(name = "kit",length = 100)
    private String kit;

    @Column(name = "schema_metadata_id")
    private Long schemaMetadataId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "features",columnDefinition = "jsonb")
    private JsonNode features;

    @Column(name = "height")
    private Double height;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "preferred_foot")
    private int preferredFoot;

    @Column(name = "captain")
    private Boolean captain;

    @Column(name = "vice_captain")
    private Boolean viceCaptain;

    @Column(name = "country_of_birth")
    private String countryOfBirth;

    @Column(name="age")
    private Integer age;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "creation_time", updatable = false)
    private Date creationTime;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private Long lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_Modified_time")
    private Date lastModifiedTime;

    @Column(name = "is_deleted")
    private int isDeleted;

    @Column(name = "is_published")
    private boolean isPublished;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "deleted_time")
    private Date deletedTime;

    @Column(name = "is_edited")
    private boolean isEdited;
}
