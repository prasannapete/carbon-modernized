package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Service;

import com.pcpl.carbon.pcplsdk.Common.Role.Model.RoleAccount;
import com.pcpl.carbon.pcplsdk.Common.Role.Response.RoleAccountResponse;

public interface RoleAccountService {
    RoleAccount save(RoleAccount roleAccount) throws Exception;
    RoleAccountResponse saveRoleAccount(RoleAccount roleAccount) throws Exception;
}
