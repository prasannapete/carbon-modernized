package com.pcpl.carbon.kitkat_service.KitkatEvents.KkApplaunched.Controller;

import com.pcpl.carbon.kitkat_service.KitkatEvents.KkApplaunched.Repository.KkAppLaunchedRepository;
import com.pcpl.carbon.kitkat_service.KitkatEvents.KkApplaunched.Service.KkAppLaunchedServiceImpl;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.Kitkat.KkAppLaunched.DTO.KkAppLaunchedDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkAppLaunched.Model.KkAppLaunched;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/events/kk-app-launched")
public class KkAppLaunchedController extends AbstractCRUDController<KkAppLaunched, KkAppLaunchedDTO, KkAppLaunchedRepository, KkAppLaunchedServiceImpl> {
}
