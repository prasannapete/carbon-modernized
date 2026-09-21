package com.pcpl.carbon.kitkat_service.KitkatEvents.KkBrandViewedGarage.Service;

import com.pcpl.carbon.kitkat_service.KitkatEvents.KkBrandViewedGarage.Repository.KkBrandViewedGarageRepository;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.Kitkat.KkBrandViewedGarage.DTO.KkBrandViewedGarageDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkBrandViewedGarage.Model.KkBrandViewedGarage;
import org.springframework.stereotype.Service;

@Service
public class KkBrandViewedGarageServiceImpl extends AbstractLazyService<KkBrandViewedGarage, KkBrandViewedGarageDTO, KkBrandViewedGarageRepository> implements KkBrandViewedGarageService {
    @Override
    public KkBrandViewedGarage getEntityObject() {
        return new KkBrandViewedGarage();
    }

    @Override
    public KkBrandViewedGarageDTO getDtoObject() {
        return new KkBrandViewedGarageDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }
}
