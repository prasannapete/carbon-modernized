package com.pcpl.carbon.kitkat_service.KitkatEvents.KkApplaunched.Service;

import com.pcpl.carbon.kitkat_service.KitkatEvents.KkApplaunched.Repository.KkAppLaunchedRepository;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.Kitkat.KkAppLaunched.DTO.KkAppLaunchedDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkAppLaunched.Model.KkAppLaunched;
import org.springframework.stereotype.Service;

@Service
public class KkAppLaunchedServiceImpl extends AbstractLazyService<KkAppLaunched, KkAppLaunchedDTO, KkAppLaunchedRepository> implements KkAppLaunchedService  {
    @Override
    public KkAppLaunched getEntityObject() {
        return new KkAppLaunched();
    }

    @Override
    public KkAppLaunchedDTO getDtoObject() {
        return new KkAppLaunchedDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }
}
