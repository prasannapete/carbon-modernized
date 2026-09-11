package com.pcpl.carbon.authserver.Tenant.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Entity
@Table(name = "cb_tenant")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Tenant {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tenant identifier. Matched (case-insensitively) against the request sub-domain
     * and also used as the login-screen folder name, e.g. tenant_name "acme" for
     * acme.host.com resolves to the template login/acme/login-page.
     */
    @Column(name = "tenant_name")
    @Length(max = 100)
    private String tenantName;

    @Column(name = "tenant_type")
    private Long tenantType;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "creation_time")
    private Date creationTime;

    @Column(name = "last_modified_by")
    private Long lastModifiedBy;

    @Column(name = "last_modified_time")
    private Date lastModifiedTime;

    @Column(name = "is_deleted")
    private int isDeleted;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "deleted_time")
    private Date deletedTime;

    @Column(name = "status")
    private Integer status;
}
