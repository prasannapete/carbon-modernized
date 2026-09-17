package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.LoyaltyOauthCredentials.Controller;

import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.LoyaltyOauthCredentialsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.LoyaltyOauthCredentials;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.LoyaltyOauthCredentials.Repository.LoyaltyOauthCredentialsRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.LoyaltyOauthCredentials.Service.LoyaltyOauthCredentialsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/loyalty-oauth-credentials")
@Slf4j
public class LoyaltyOauthCredentialsController extends AbstractCRUDController<LoyaltyOauthCredentials, LoyaltyOauthCredentialsDTO, LoyaltyOauthCredentialsRepository, LoyaltyOauthCredentialsServiceImpl> {

}
