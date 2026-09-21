package com.pcpl.carbon.kitkat_service.KitkatEvents.KkRacePlayed.Controller;


import com.pcpl.carbon.kitkat_service.KitkatEvents.KkRacePlayed.Repository.KkRacePlayedRepository;
import com.pcpl.carbon.kitkat_service.KitkatEvents.KkRacePlayed.Service.KkRacePlayedService;
import com.pcpl.carbon.kitkat_service.KitkatEvents.KkRacePlayed.Service.KkRacePlayedServiceImpl;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.Kitkat.KkRacePlayed.DTO.KkRacePlayedDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkRacePlayed.Model.KkRacePlayed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/events/kk-race-played")
public class KkRacePlayedController extends AbstractCRUDController<KkRacePlayed, KkRacePlayedDTO, KkRacePlayedRepository, KkRacePlayedServiceImpl> {
    @Autowired
    KkRacePlayedService kkRacePlayedService;

    @RequestMapping(value = "/race-info", method = RequestMethod.POST)
    public ApplicationResponse getRaceInfo(@RequestBody(required = false) Map<String, String> formData) {
        Integer consoleId = null;
        if (formData != null && formData.get("consoleId") != null && !formData.get("consoleId").trim().isEmpty()) {
            consoleId = Integer.valueOf(formData.get("consoleId"));
        }
        return kkRacePlayedService.getRaceInfo(consoleId);
    }
}
