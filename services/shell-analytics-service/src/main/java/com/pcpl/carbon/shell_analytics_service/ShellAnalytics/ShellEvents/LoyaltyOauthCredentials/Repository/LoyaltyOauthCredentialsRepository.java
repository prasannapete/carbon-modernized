package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.LoyaltyOauthCredentials.Repository;


import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.LoyaltyOauthCredentials;

import java.util.Optional;

public interface LoyaltyOauthCredentialsRepository extends PCPLCRUDRepository<LoyaltyOauthCredentials> {

    Optional<LoyaltyOauthCredentials> findByCountry(String countryCode);
}
