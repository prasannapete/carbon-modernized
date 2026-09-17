package com.pcpl.carbon.shell_analytics_service.ClientDetails.Service;

import com.pcpl.carbon.pcplsdk.ClientDetails.DTO.ClientDetailsDTO;
import com.pcpl.carbon.pcplsdk.ClientDetails.Model.ClientDetails;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.shell_analytics_service.ClientDetails.Repository.ClientDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientDetailsServiceImpl extends AbstractLazyService<ClientDetails, ClientDetailsDTO, ClientDetailsRepository> implements ClientDetailsService {
    @Override
    public ClientDetails getEntityObject() {
        return new ClientDetails();
    }

    @Override
    public ClientDetailsDTO getDtoObject() {
        return new ClientDetailsDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getCountryCode";
    }
}
