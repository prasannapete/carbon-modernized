package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Controller;

import com.pcpl.carbon.pcplsdk.Common.Role.DTO.RoleDTO;
import com.pcpl.carbon.pcplsdk.Common.Role.Model.Role;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Repository.RoleRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Service.RoleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/role")
@Slf4j
public class RoleController extends AbstractCRUDController<Role, RoleDTO, RoleRepository, RoleServiceImpl> {
}
