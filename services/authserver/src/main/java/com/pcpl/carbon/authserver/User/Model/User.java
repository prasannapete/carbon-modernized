package com.pcpl.carbon.authserver.User.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "cb_user")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

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

    @JsonIgnore
    @Column(name = "mobile_number", columnDefinition = "VARCHAR(30)")
    private String mobileNumber;

    @JsonIgnore
    @Column(name = "alternative_phone", columnDefinition = "VARCHAR(30)")
    private String alternativePhone;

    @JsonIgnore
    @Column(name = "email_address", columnDefinition = "VARCHAR(255)")
    @Length(max = 255, min = 1, message = "Email address length should be between 1 and 255")
    @NotEmpty(message = "Email address cannot be empty")
    @NotBlank(message = "Email address cannot be blank")
    @NotNull(message = "Email address cannot be empty")
    private String emailAddress;

    @JsonIgnore
    @Column(name = "photo_id")
    private Long photoId;

    @JsonIgnore
    @Column(name = "user_name")
    @Length(max = 255, min = 1, message = "User name length should be between 1 and 255")
    @NotEmpty(message = "User Name cannot be empty")
    @NotBlank(message = "User Name cannot be blank")
    @NotNull(message = "User Name cannot be empty")
    private String userName;

    @JsonIgnore
    @Column(name = "password")
    @Length(max = 1000)
    private String password;

    @JsonIgnore
    @Column(name = "account_status")
    private int accountStatus;

    @JsonIgnore
    @Column(name = "is_god")
    private int isGod;

    @JsonIgnore
    @Column(name = "created_by")
    @Length(max = 40)
    private String createdBy;

    @JsonIgnore
    @Column(name = "creation_time")
    private Date creationTime;

    @JsonIgnore
    @Column(name = "last_modified_by")
    @Length(max = 40)
    private String lastModifiedBy;

    @JsonIgnore
    @Column(name = "last_modified_time")
    private Date lastModifiedTime;

    @JsonIgnore
    @Column(name = "is_deleted")
    private int isDeleted;

    @JsonIgnore
    @Column(name = "deleted_by")
    @Length(max = 40)
    private String deletedBy;

    @JsonIgnore
    @Column(name = "deleted_time")
    private Date deletedTime;


    public User(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.mobileNumber = user.getMobileNumber();
        this.emailAddress = user.getEmailAddress();
        this.accountStatus = user.getAccountStatus();
        this.isGod = user.getIsGod();
        this.createdBy = user.getCreatedBy();
        this.creationTime = user.getCreationTime();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedTime = user.getLastModifiedTime();
        this.isDeleted = user.getIsDeleted();
        this.deletedBy = user.getDeletedBy();
        this.deletedTime = user.getDeletedTime();
    }
}