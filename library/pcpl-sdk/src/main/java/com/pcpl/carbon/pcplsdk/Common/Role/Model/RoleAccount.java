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
    // IDENTITY (DB-generated) so the insert doesn't depend on a Hibernate sequence that may not
    // resolve; cb_role_accounts.id must be GENERATED ... AS IDENTITY to match. Without this the
    // RoleAccount insert failed on id generation (swallowed by saveRoleAccount's try/catch), so
    // user role assignments were never persisted.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "role_id")
    private Long roleId;
}
