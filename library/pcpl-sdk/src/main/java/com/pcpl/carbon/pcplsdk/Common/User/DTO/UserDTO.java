package com.pcpl.carbon.pcplsdk.Common.User.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pcpl.carbon.pcplsdk.Common.Role.Model.Role;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private Long id;
    private Long tenantId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String alternativePhone;
    private String emailAddress;
    private Long photoId;
    private String userName;
    private String password;
    private int accountStatus;
    private int isGod;
    private List<Role> roles;
    private Long roleId;
    private List<UserCountriesDTO> userCountries;
    private String countryIds;
    private Long createdBy;
    private Date creationTime;
    private Long lastModifiedBy;
    private Date lastModifiedTime;
    private int isDeleted;
    private Long deletedBy;
    private Date deletedTime;
    private int isPlayMobilUser;


    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.mobileNumber = user.getMobileNumber();
        this.alternativePhone = user.getAlternativePhone();
        this.emailAddress = user.getEmailAddress();
        this.photoId = user.getPhotoId();
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.accountStatus = user.getAccountStatus();
        this.isGod = user.getIsGod();
        this.roles = user.getRoles();
        if (this.roles != null && !this.roles.isEmpty()) {
            this.roleId = this.roles.get(0).getId();
        }
        this.isPlayMobilUser = user.getIsPlayMobilUser();
    }

    public UserDTO(Long id, String firstName, String lastName, String mobileNumber, String alternativePhone, String emailAddress, Long photoId, String userName, String password, int accountStatus, int isGod,int isPlayMobilUser) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.alternativePhone = alternativePhone;
        this.emailAddress = emailAddress;
        this.photoId = photoId;
        this.userName = userName;
        this.password = password;
        this.accountStatus = accountStatus;
        this.isGod = isGod;
        this.isPlayMobilUser = isPlayMobilUser;
    }
}
