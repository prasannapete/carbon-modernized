package com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import org.hibernate.annotations.Filter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Entity
@Table(name = "cb_schema_metadata")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class SchemaMetadata extends ApplicationModel {

    @Column(name = "tenant_id" ,nullable = false)
    private Long tenantId;

    @Column(name = "schema_version" ,nullable = false)
    private Double schemaVersion;

    @LastModifiedDate
    @Column(name = "updated_at" ,nullable = false)
    private Date updatedAt;
}
