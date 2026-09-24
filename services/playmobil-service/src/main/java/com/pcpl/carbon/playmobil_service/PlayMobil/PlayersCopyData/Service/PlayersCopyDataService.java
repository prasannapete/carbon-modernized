package com.pcpl.carbon.playmobilservice.PlayMobil.PlayersCopyData.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.response.PlayersResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.PlayersCopyData.response.PlayersCopyDataResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO.SchemaMetadataDTO;

public interface PlayersCopyDataService {

    ApplicationResponse savePlayersCopyData(SchemaMetadataDTO schemaMetadataDTO);
    PlayersCopyDataResponse deletePlayersCopyData(SchemaMetadataDTO schemaMetadataDTO);
}
