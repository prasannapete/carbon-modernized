package com.pcpl.carbon.authserver.User.Model;
import com.pcpl.carbon.authserver.AppFeature.Model.AppFeature;
import com.pcpl.carbon.authserver.AppFeature.Service.AppFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetail  extends User implements UserDetails, Serializable {

    @Transient
    private List<GrantedAuthority> authorities;

    private List<AppFeature> appFeatures;

    @Autowired
    AppFeatureService appFeatureService;

    public UserDetail(User user) {
        super(user);
    }
    public UserDetail(User user, List<AppFeature> appFeatures) {
        super(user);
        this.appFeatures = appFeatures;
    }
    public UserDetail(
            Long id,
            Long orgId,
            Long clientId,
            String firstName,
            String lastName,
            String userName,
            String password,
            int accountStatus,
            int isGod,
            List<GrantedAuthority> authorities
    ) {
        this.setId(id);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setUserName(userName);
        this.setPassword(password);
        this.setAccountStatus(accountStatus);
        this.setIsGod(isGod);
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        try {
            for (AppFeature assignedAppFeature : appFeatures) {
                grantedAuthorityList.add(new SimpleGrantedAuthority(assignedAppFeature.getSystemRole()));
            }
        }catch (Exception ex){

        }
        if(this.getIsGod() == 1){
            grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_GOD"));

        }
        grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        this.authorities = grantedAuthorityList;
        return  grantedAuthorityList;
    }

    @Override
    public String getUsername() {
        return super.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return super.getIsDeleted() == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return super.getAccountStatus() == 2;
    }
}
