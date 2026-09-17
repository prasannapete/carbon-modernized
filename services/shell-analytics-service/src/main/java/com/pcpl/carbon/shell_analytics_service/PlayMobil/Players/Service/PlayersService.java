package com.pcpl.carbon.shell_analytics_service.PlayMobil.Players.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.response.PlayersResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO.SchemaMetadataDTO;

public interface PlayersService {
    ApplicationResponse savePlayerData(SchemaMetadataDTO schemaMetadataDTO);
    PlayersResponse deletePlayerData(SchemaMetadataDTO schemaMetadataDTO);
    PlayersResponse getAllPlayers();
}
