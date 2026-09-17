package com.pcpl.carbon.shell_analytics_service.ClientDetails.Controller;

import com.pcpl.carbon.pcplsdk.ClientDetails.DTO.ClientDetailsDTO;
import com.pcpl.carbon.pcplsdk.ClientDetails.Model.ClientDetails;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.shell_analytics_service.ClientDetails.Repository.ClientDetailsRepository;
import com.pcpl.carbon.shell_analytics_service.ClientDetails.Service.ClientDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/client-details")
@Slf4j
public class ClientDetailsController extends AbstractCRUDController<ClientDetails, ClientDetailsDTO, ClientDetailsRepository, ClientDetailsServiceImpl> {
}
