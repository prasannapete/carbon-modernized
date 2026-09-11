package com.pcpl.carbon.authserver.Role.Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Entity
@Table(name = "cb_role_grants")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class RoleGrant  implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "app_feature_id")
    private Long appFeatureId;
}
