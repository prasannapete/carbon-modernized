package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.LoyaltyOauthCredentials.Service;

import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.LoyaltyOauthCredentialsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.LoyaltyOauthCredentials;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.LoyaltyOauthCredentials.Repository.LoyaltyOauthCredentialsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoyaltyOauthCredentialsServiceImpl extends AbstractLazyService<LoyaltyOauthCredentials, LoyaltyOauthCredentialsDTO, LoyaltyOauthCredentialsRepository> implements LoyaltyOauthCredentialsService {

    @Override
    public LoyaltyOauthCredentials getEntityObject() {
       return new LoyaltyOauthCredentials();
    }

    @Override
    public LoyaltyOauthCredentialsDTO getDtoObject() {
        return new LoyaltyOauthCredentialsDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }
}
