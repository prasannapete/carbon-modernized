package com.pcpl.carbon.pcplsdk.Common.User.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import com.pcpl.carbon.pcplsdk.Common.Role.Model.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
@Table(name = "cb_user")
@Data
@ToString
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends ApplicationModel {

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "first_name")
    @Length(max = 100, min = 1, message = "First name length should be between 1 and 100")
    @NotEmpty(message = "First Name cannot be empty")
    @NotBlank(message = "First Name cannot be blank")
    @NotNull(message = "First Name cannot be empty")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "mobile_number", columnDefinition = "VARCHAR(30)")
    private String mobileNumber;

    @Column(name = "alternative_phone", columnDefinition = "VARCHAR(30)")
    private String alternativePhone;

    @Column(name = "email_address", columnDefinition = "VARCHAR(255)")
    @Length(max = 255, min = 1, message = "Email address length should be between 1 and 255")
    @NotEmpty(message = "Email address cannot be empty")
    @NotBlank(message = "Email address cannot be blank")
    @NotNull(message = "Email address cannot be empty")
    private String emailAddress;

    @Column(name = "photo_id")
    private Long photoId;

    @Column(name = "user_name")
    @Length(max = 255, min = 1, message = "User name length should be between 1 and 255")
    @NotEmpty(message = "User Name cannot be empty")
    @NotBlank(message = "User Name cannot be blank")
    @NotNull(message = "User Name cannot be empty")
    private String userName;

    @Column(name = "password")
    @Length(max = 1000)
    private String password;

    @Column(name = "account_status")
    private int accountStatus;

    @Column(name = "is_god")
    private int isGod;

    @Column(name = "is_play_mobil_user")
    private int isPlayMobilUser;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cb_role_accounts",
            joinColumns = {
                    @JoinColumn(name = "account_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "role_id", referencedColumnName = "id")
            }
    )
    @Fetch(value = FetchMode.SUBSELECT)
    @ToString.Exclude
    private List<Role> roles;
}
