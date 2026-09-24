package com.pcpl.carbon.playmobilservice.PlayMobil.PlayersCopyData.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.PlayersCopyData.response.PlayersCopyDataResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO.SchemaMetadataDTO;
import com.pcpl.carbon.playmobilservice.PlayMobil.PlayersCopyData.Service.PlayersCopyDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/players-copy-data")
@RequiredArgsConstructor
public class PlayersCopyDataController {

    private final PlayersCopyDataService playersCopyDataService;

    @RequestMapping(value = "/save-data", method = RequestMethod.POST)
    public ApplicationResponse savePlayersCopyData(@RequestBody SchemaMetadataDTO formData) throws Exception {

        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = playersCopyDataService.savePlayersCopyData(formData);

        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Players copy data saved successfully!");

        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        return applicationResponse;
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public PlayersCopyDataResponse deletePlayersCopyData(@RequestBody SchemaMetadataDTO formData) throws Exception {

        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        PlayersCopyDataResponse applicationResponse = PlayersCopyDataResponse.builder().build();

        applicationResponse = playersCopyDataService.deletePlayersCopyData(formData);

        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Players copy data deleted successfully!");

        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());

        return applicationResponse;
    }
}
