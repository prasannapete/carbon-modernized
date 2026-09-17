package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Service;

import com.pcpl.carbon.pcplsdk.Common.Role.DTO.RoleDTO;
import com.pcpl.carbon.pcplsdk.Common.Role.Model.Role;
import com.pcpl.carbon.pcplsdk.Common.User.DTO.UserDTO;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleServiceImpl extends AbstractLazyService<Role, RoleDTO, RoleRepository> implements RoleService {

    @Override
    public Role getEntityObject() {
        return new Role();
    }

    @Override
    public RoleDTO getDtoObject() {
        return new RoleDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }
}
