package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserToken.Service;

import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.UserTokenDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.UserToken;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserToken.Repository.UserTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class UserTokenServiceImpl extends AbstractLazyService<UserToken, UserTokenDTO, UserTokenRepository> implements UserTokenService {
    @Override
    public UserToken getEntityObject() {
        return new UserToken();
    }

    @Override
    public UserTokenDTO getDtoObject() {
        return new UserTokenDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }
}
