package com.pcpl.carbon.pcplsdk.PlayMobil.Players.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayersDTO {
    private String id;
    private String optaId;
    private Long tenantId;
    private String name;
    private String shirtName;
    private Integer number;
    private Integer position;
    private String team;
    private LocalDate dob;
    private String nationality;
    private String homeTown;
    private String kit;
    private Long schemaMetadataId;
    private JsonNode features;
    private Double height;
    private Double weight;
    private Integer preferredFoot;
    private Boolean captain;
    private Boolean viceCaptain;
    private String countryOfBirth;
    private Integer age;
    private Long createdBy;
    private Date creationTime;
    private Long lastModifiedBy;
    private Date lastModifiedTime;
    private int isDeleted;
    private boolean isPublished;
    private Long deletedBy;
    private Date deletedTime;
    private boolean isEdited;
}
