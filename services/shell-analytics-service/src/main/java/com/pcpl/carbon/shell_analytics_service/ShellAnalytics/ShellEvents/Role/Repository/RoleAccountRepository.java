package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Repository;

import com.pcpl.carbon.pcplsdk.Common.Role.Model.RoleAccount;
import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleAccountRepository extends PCPLCRUDRepository<RoleAccount> {
    Optional<RoleAccount> findAllByAccountId(Long accountId);
}
