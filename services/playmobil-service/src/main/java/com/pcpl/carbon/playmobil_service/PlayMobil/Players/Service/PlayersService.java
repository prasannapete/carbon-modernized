package com.pcpl.carbon.playmobilservice.PlayMobil.Players.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.response.PlayersResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO.SchemaMetadataDTO;
import org.springframework.web.multipart.MultipartFile;

public interface PlayersService {
    ApplicationResponse savePlayerData(SchemaMetadataDTO schemaMetadataDTO);
    PlayersResponse deletePlayerData(SchemaMetadataDTO schemaMetadataDTO);
    PlayersResponse getAllPlayers();
    ApplicationResponse publishPlayersByTeamCode(String teamCode);
    ApplicationResponse importPlayers(MultipartFile playerFile, MultipartFile catalogFile);
}
