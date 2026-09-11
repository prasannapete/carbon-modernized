package com.pcpl.carbon.authserver.Role.Model;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Entity
@Table(name = "cb_role_accounts")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class RoleAccount implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "role_id")
    private Long roleId;
}
