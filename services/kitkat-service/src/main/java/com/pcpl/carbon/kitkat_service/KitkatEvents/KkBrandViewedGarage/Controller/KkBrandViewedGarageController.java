package com.pcpl.carbon.kitkat_service.KitkatEvents.KkBrandViewedGarage.Controller;

import com.pcpl.carbon.kitkat_service.KitkatEvents.KkBrandViewedGarage.Repository.KkBrandViewedGarageRepository;
import com.pcpl.carbon.kitkat_service.KitkatEvents.KkBrandViewedGarage.Service.KkBrandViewedGarageServiceImpl;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.Kitkat.KkBrandViewedGarage.DTO.KkBrandViewedGarageDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkBrandViewedGarage.Model.KkBrandViewedGarage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/events/kk-brand-viewed-garage")
public class KkBrandViewedGarageController extends AbstractCRUDController<KkBrandViewedGarage, KkBrandViewedGarageDTO, KkBrandViewedGarageRepository, KkBrandViewedGarageServiceImpl> {
}
