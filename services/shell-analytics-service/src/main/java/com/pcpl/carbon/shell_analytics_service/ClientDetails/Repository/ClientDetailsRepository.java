package com.pcpl.carbon.shell_analytics_service.ClientDetails.Repository;

import com.pcpl.carbon.pcplsdk.ClientDetails.Model.ClientDetails;
import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;

import java.util.Optional;

public interface ClientDetailsRepository extends PCPLCRUDRepository<ClientDetails> {
    Optional<ClientDetails> findByCountryCodeAndIsDeleted(String countryCode, int isDeleted);
}
