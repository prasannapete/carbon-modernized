package com.pcpl.carbon.authserver.Role.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "cb_roles")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Role implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "client_id")
    @Length(max = 40)
    private String clientId;

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

    @Column(name = "created_by")
    @Length(max = 40)
    private String createdBy;

    @Column(name = "creation_time")
    private Date creationTime;

    @Column(name = "last_modified_by")
    @Length(max = 40)
    private String lastModifiedBy;

    @Column(name = "last_Modified_time")
    private Date lastModifiedTime;

    @Column(name = "is_deleted")
    private int isDeleted;

    @Column(name = "deleted_by")
    @Length(max = 40)
    private String deletedBy;

    @Column(name = "deleted_time")
    private Date deletedTime;
}
