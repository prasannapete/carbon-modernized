package com.pcpl.carbon.pcplsdk.Common.Role.Model;

import com.pcpl.carbon.pcplsdk.Common.AppFeature.Model.AppFeature;
import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
@Table(name = "cb_roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role extends ApplicationModel {
    @Column(name = "client_id")
    @Length(max = 40)
    private String clientId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "name")
    @Length(max = 255, min = 1, message = "Role name length should be between 1 and 100")
    @NotNull(message = "Role Name cannot be null")
    @NotEmpty(message = "Role name cannot be empty")
    @NotBlank(message = "Role name cannot be blank")
    private String name;

    @Column(name = "home_page")
    @Length(max = 255)
    private String homePage;

    @Column(name = "description")
    @Length(max = 1000)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "srl_role_grants",
            joinColumns = {
                    @JoinColumn(name = "role_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "app_feature_id", referencedColumnName = "id")
            }
    )
    private List<AppFeature> appFeatures;

}
