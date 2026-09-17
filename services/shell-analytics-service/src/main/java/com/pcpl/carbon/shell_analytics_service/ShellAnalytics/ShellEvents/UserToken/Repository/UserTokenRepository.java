package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserToken.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.UserToken;

public interface UserTokenRepository  extends PCPLCRUDRepository<UserToken> {
    UserToken findFirstByConsumerUuidAndIsDeletedOrderByCreationTimeDesc(String customerUuid, int i);
}
