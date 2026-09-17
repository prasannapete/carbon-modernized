package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Controller;


import com.pcpl.carbon.pcplsdk.Common.Role.DTO.RoleAccountDTO;
import com.pcpl.carbon.pcplsdk.Common.Role.Model.RoleAccount;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Repository.RoleAccountRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Service.RoleAccountServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/role-account")
public class RoleAccountController extends AbstractCRUDController<RoleAccount, RoleAccountDTO, RoleAccountRepository, RoleAccountServiceImpl> {
}
