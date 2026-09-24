package com.pcpl.carbon.playmobilservice.PlayMobil.Players.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.response.PlayersResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO.SchemaMetadataDTO;
import com.pcpl.carbon.playmobilservice.PlayMobil.Players.Service.PlayersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(value = "/players")
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

    @RequestMapping(value = "/publish-by-team-code", method = RequestMethod.POST)
    public ApplicationResponse publishPlayersByTeamCode(@RequestParam String teamCode) {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = playersService.publishPlayersByTeamCode(teamCode);
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ApplicationResponse importPlayers(
            @RequestParam("playerFile") MultipartFile playerFile,
            @RequestParam(value = "catalogFile", required = false) MultipartFile catalogFile
    ) {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = playersService.importPlayers(playerFile, catalogFile);
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
