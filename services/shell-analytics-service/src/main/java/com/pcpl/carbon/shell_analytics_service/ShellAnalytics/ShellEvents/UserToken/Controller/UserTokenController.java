package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserToken.Controller;

import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.UserTokenDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.UserToken;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserToken.Repository.UserTokenRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserToken.Service.UserTokenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/srl/user-token")
@Slf4j
public class UserTokenController  extends AbstractCRUDController<UserToken, UserTokenDTO, UserTokenRepository, UserTokenServiceImpl> {
}
