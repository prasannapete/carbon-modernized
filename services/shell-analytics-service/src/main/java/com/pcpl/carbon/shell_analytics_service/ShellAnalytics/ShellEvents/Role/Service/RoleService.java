package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Service;

import com.pcpl.carbon.pcplsdk.Common.Role.DTO.RoleDTO;
import com.pcpl.carbon.pcplsdk.Common.Role.Model.Role;
import com.pcpl.carbon.pcplsdk.Generic.Service.PCPLCRUDService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Repository.RoleRepository;

public interface RoleService extends PCPLCRUDService<Role, RoleDTO, RoleRepository> {
}
