package com.pcpl.carbon.kitkat_service.KitkatEvents.KkCarUnlocked.Service;

import com.pcpl.carbon.kitkat_service.KitkatEvents.KkCarUnlocked.Repository.KkCarUnlockedRepository;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.Kitkat.KkCarUnlocked.DTO.KkCarUnlockedDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkCarUnlocked.Model.KkCarUnlocked;
import org.springframework.stereotype.Service;

@Service
public class KkCarUnlockedServiceImpl extends AbstractLazyService<KkCarUnlocked, KkCarUnlockedDTO, KkCarUnlockedRepository> implements KkCarUnlockedService{
    @Override
    public KkCarUnlocked getEntityObject() {
        return new KkCarUnlocked();
    }

    @Override
    public KkCarUnlockedDTO getDtoObject() {
        return new KkCarUnlockedDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }
}
