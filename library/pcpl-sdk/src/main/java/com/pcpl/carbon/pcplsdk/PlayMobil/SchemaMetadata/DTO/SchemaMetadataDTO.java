package com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO;

import com.pcpl.carbon.pcplsdk.PlayMobil.Players.DTO.PlayersDTO;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.Model.Players;
import com.pcpl.carbon.pcplsdk.PlayMobil.PlayersCopyData.Model.PlayersCopyData;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaMetadataDTO {
    private Long id;
    private Long tenantId;
    private Double schemaVersion;
    private Date updatedAt;
    private Long createdBy;
    private Date creationTime;
    private Long lastModifiedBy;
    private Date lastModifiedTime;
    private int isDeleted;
    private Long deletedBy;
    private Date deletedTime;
    private Players player;
    private PlayersCopyData playersCopyData;
}
