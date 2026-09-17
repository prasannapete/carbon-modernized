package com.pcpl.carbon.shell_analytics_service.PlayMobil.Players.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.DTO.PlayersDTO;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.Model.Players;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.response.PlayersResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO.SchemaMetadataDTO;
import com.pcpl.carbon.shell_analytics_service.PlayMobil.Players.Repository.PlayersRepository;
import com.pcpl.carbon.shell_analytics_service.PlayMobil.Players.Service.PlayersService;
import com.pcpl.carbon.shell_analytics_service.PlayMobil.Players.Service.PlayersServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/play-mobil/players")
@RequiredArgsConstructor
public class PlayersController {
    private final PlayersService playersService;

    @RequestMapping(value = "/save-data",method = RequestMethod.POST)
    public ApplicationResponse savePlayers(@RequestBody SchemaMetadataDTO formData) throws  Exception{
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = playersService.savePlayerData(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public PlayersResponse deletePlayer(@RequestBody SchemaMetadataDTO formData) throws  Exception{
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        PlayersResponse applicationResponse = PlayersResponse.builder().build();

        applicationResponse = playersService.deletePlayerData(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

    @RequestMapping(value = "/get-all",method = RequestMethod.POST)
    public PlayersResponse getAll() throws  Exception{
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        PlayersResponse applicationResponse = PlayersResponse.builder().build();

        applicationResponse = playersService.getAllPlayers();
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
