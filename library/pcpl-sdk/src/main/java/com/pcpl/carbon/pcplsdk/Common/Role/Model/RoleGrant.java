package com.pcpl.carbon.pcplsdk.Common.Role.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cb_role_grants")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleGrant {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "app_feature_id")
    private Long appFeatureId;
}
