package com.pcpl.carbon.shell_analytics_service.PlayMobil.Players.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.DTO.PlayersDTO;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.Model.Players;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.response.PlayersResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO.SchemaMetadataDTO;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.Model.SchemaMetadata;
import com.pcpl.carbon.shell_analytics_service.PlayMobil.Players.Repository.PlayersRepository;
import com.pcpl.carbon.shell_analytics_service.PlayMobil.SchemaMetadata.Repository.SchemaMetadataRepository;
import com.pcpl.carbon.shell_analytics_service.PlayMobil.SchemaMetadata.Service.SchemaMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class PlayersServiceImpl implements PlayersService{
    private final SchemaMetadataRepository schemaMetadataRepository;
    private final SchemaMetadataService schemaMetadataService;
    private final PlayersRepository playersRepository;
    private final ObjectMapper objectMapper;
    @Override
    public ApplicationResponse savePlayerData(SchemaMetadataDTO schemaMetadataDTO) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        try{
            Optional<SchemaMetadata> schemaMetadata = schemaMetadataRepository.findBySchemaVersion(schemaMetadataDTO.getSchemaVersion());
            if(!schemaMetadata.isEmpty()){
                schemaMetadataDTO.setId(schemaMetadata.get().getId());
            }
            schemaMetadataService.save(schemaMetadataDTO);
            schemaMetadataDTO.getPlayer().setSchemaMetadataId(schemaMetadataDTO.getId());
            Optional<Players> optionalPlayers = playersRepository.findById(schemaMetadataDTO.getPlayer().getId());
            if(!optionalPlayers.isPresent()){
                schemaMetadataDTO.getPlayer().setCreationTime(new Date());
            }else{
                schemaMetadataDTO.getPlayer().setCreationTime(optionalPlayers.get().getCreationTime());
                schemaMetadataDTO.getPlayer().setLastModifiedTime(new Date());
            }
            schemaMetadataDTO.getPlayer().setIsDeleted(0);
            Players player = playersRepository.save(schemaMetadataDTO.getPlayer());
            applicationResponse.setData(player);
            applicationResponse.setSuccess(true);
        }catch (Exception e){
            applicationResponse.setSuccess(false);
            applicationResponse.setMessage(e.getMessage());
        }
        return applicationResponse;
    }

    @Override
    public PlayersResponse deletePlayerData(SchemaMetadataDTO schemaMetadataDTO) {
        PlayersResponse applicationResponse = PlayersResponse.builder().build();
        try{
            Players player = playersRepository.findById(schemaMetadataDTO.getPlayer().getId()).get();
            player.setIsDeleted(1);
            playersRepository.save(player);
            applicationResponse.setSuccess(true);
        }catch (Exception e){
            applicationResponse.setSuccess(false);
            applicationResponse.setMessage(e.getMessage());
        }
        return applicationResponse;
    }

    @Override
    public PlayersResponse getAllPlayers() {
        PlayersResponse applicationResponse = PlayersResponse.builder().build();
        try{
            List<Players> players = playersRepository.findAllByIsDeletedOrderByCreationTime(0);
            applicationResponse.setPlayers(players);
            applicationResponse.setSuccess(true);
        }catch (Exception e){
            applicationResponse.setSuccess(false);
            applicationResponse.setMessage(e.getMessage());
        }
        return applicationResponse;
    }
}
