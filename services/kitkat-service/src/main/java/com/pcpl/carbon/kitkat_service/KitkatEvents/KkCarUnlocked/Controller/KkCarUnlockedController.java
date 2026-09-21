package com.pcpl.carbon.kitkat_service.KitkatEvents.KkCarUnlocked.Controller;

import com.pcpl.carbon.kitkat_service.KitkatEvents.KkCarUnlocked.Repository.KkCarUnlockedRepository;
import com.pcpl.carbon.kitkat_service.KitkatEvents.KkCarUnlocked.Service.KkCarUnlockedServiceImpl;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.Kitkat.KkCarUnlocked.DTO.KkCarUnlockedDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkCarUnlocked.Model.KkCarUnlocked;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/events/kk-car-unlocked")
public class KkCarUnlockedController extends AbstractCRUDController<KkCarUnlocked, KkCarUnlockedDTO, KkCarUnlockedRepository, KkCarUnlockedServiceImpl> {
}
