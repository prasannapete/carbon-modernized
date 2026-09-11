package com.pcpl.carbon.pcplsdk.Common.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * Base for tenant-aware entities. Declares the reusable Hibernate "tenantFilter"
 * (a {@code tenant_id = :tenantId} row filter). Concrete tenant-aware entities opt in
 * with {@code @Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")}; the
 * filter is enabled per request with the logged-in user's tenant by common-service's
 * TenantInterceptor, giving generic tenant data isolation without per-repository queries.
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
public abstract class ApplicationModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "deleted_time")
    private Date deletedTime;
}
