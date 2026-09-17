package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Service;

import com.pcpl.carbon.pcplsdk.Common.Role.DTO.RoleAccountDTO;
import com.pcpl.carbon.pcplsdk.Common.Role.Model.RoleAccount;
import com.pcpl.carbon.pcplsdk.Common.Role.Response.RoleAccountResponse;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Repository.RoleAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RoleAccountServiceImpl extends AbstractLazyService<RoleAccount, RoleAccountDTO, RoleAccountRepository> implements RoleAccountService {

    @Autowired
    RoleAccountRepository roleAccountRepository;

    @Override
    public RoleAccount getEntityObject() {
        return new RoleAccount();
    }

    @Override
    public RoleAccountDTO getDtoObject() {
        return new RoleAccountDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }
    @Override
    public RoleAccount save(RoleAccount roleAccount) throws Exception {
        return roleAccountRepository.save(roleAccount);
    }

    @Override
    public RoleAccountResponse saveRoleAccount(RoleAccount roleAccount) throws Exception {
        log.trace("Entering");
        RoleAccountResponse roleAccountResponse = new RoleAccountResponse();

        try {
            Optional<RoleAccount> optionalRoleAccount = roleAccountRepository.findAllByAccountId(roleAccount.getAccountId());

            if (optionalRoleAccount.isPresent()) {
                roleAccount.setId(optionalRoleAccount.get().getId());

                roleAccountResponse.setRoleAccount(
                        convertEntityToDto(
                                this.save(roleAccount)
                        ));
                roleAccountResponse.setSuccess(true);
                roleAccountResponse.setError("");
            } else {
                roleAccountResponse.setRoleAccount(
                        convertEntityToDto(
                                this.save(roleAccount)
                        ));
                roleAccountResponse.setSuccess(true);
                roleAccountResponse.setError("");
            }
            log.trace("Completed Successfully");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            roleAccountResponse.setSuccess(false);
            roleAccountResponse.setError(ex.getMessage());
        }
        return roleAccountResponse;
    }

}
