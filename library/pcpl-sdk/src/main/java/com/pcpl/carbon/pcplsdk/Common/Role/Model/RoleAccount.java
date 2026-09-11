package com.pcpl.carbon.pcplsdk.Common.Role.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cb_role_accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAccount {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "tenant_id")
    private Long tenantId;
}
