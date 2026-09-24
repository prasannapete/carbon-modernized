package com.pcpl.carbon.playmobilservice.PlayMobil.PlayersCopyData.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.PlayersCopyData.Model.PlayersCopyData;
import com.pcpl.carbon.pcplsdk.PlayMobil.PlayersCopyData.response.PlayersCopyDataResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO.SchemaMetadataDTO;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.Model.SchemaMetadata;
import com.pcpl.carbon.playmobilservice.PlayMobil.Config.TenantSupport;
import com.pcpl.carbon.playmobilservice.PlayMobil.PlayersCopyData.Repository.PlayersCopyDataRepository;
import com.pcpl.carbon.playmobilservice.PlayMobil.SchemaMetadata.Repository.SchemaMetadataRepository;
import com.pcpl.carbon.playmobilservice.PlayMobil.SchemaMetadata.Service.SchemaMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayersCopyDataServiceImpl implements PlayersCopyDataService {

    private final PlayersCopyDataRepository playersCopyDataRepository;
    private final SchemaMetadataRepository schemaMetadataRepository;
    private final SchemaMetadataService schemaMetadataService;


    @Override
    public ApplicationResponse savePlayersCopyData(SchemaMetadataDTO schemaMetadataDTO) {

        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        try {

            Optional<SchemaMetadata> schemaMetadata =
                    schemaMetadataRepository.findBySchemaVersion(schemaMetadataDTO.getSchemaVersion());

            if (!schemaMetadata.isEmpty()) {
                schemaMetadataDTO.setId(schemaMetadata.get().getId());
            }

            schemaMetadataService.save(schemaMetadataDTO);

            schemaMetadataDTO.getPlayersCopyData().setSchemaMetadataId(schemaMetadataDTO.getId());

            Optional<PlayersCopyData> optionalPlayersCopyData =
                    playersCopyDataRepository.findById(schemaMetadataDTO.getPlayersCopyData().getId());

            if (!optionalPlayersCopyData.isPresent()) {

                schemaMetadataDTO.getPlayersCopyData().setCreationTime(new Date());

            } else {

                schemaMetadataDTO.getPlayersCopyData()
                        .setCreationTime(optionalPlayersCopyData.get().getCreationTime());

                schemaMetadataDTO.getPlayersCopyData()
                        .setLastModifiedTime(new Date());
            }

            schemaMetadataDTO.getPlayersCopyData().setIsDeleted(0);

            PlayersCopyData playersCopyData =
                    playersCopyDataRepository.save(TenantSupport.stampTenant(schemaMetadataDTO.getPlayersCopyData()));

            applicationResponse.setData(playersCopyData);
            applicationResponse.setSuccess(true);

        } catch (Exception e) {

            applicationResponse.setSuccess(false);
            applicationResponse.setMessage(e.getMessage());
        }

        return applicationResponse;
    }

    @Override
    public PlayersCopyDataResponse deletePlayersCopyData(SchemaMetadataDTO schemaMetadataDTO) {

        PlayersCopyDataResponse applicationResponse = PlayersCopyDataResponse.builder().build();

        try {

            PlayersCopyData playersCopyData =
                    playersCopyDataRepository
                            .findById(schemaMetadataDTO.getPlayersCopyData().getId())
                            .get();

            playersCopyData.setIsDeleted(1);

            playersCopyDataRepository.save(playersCopyData);

            applicationResponse.setSuccess(true);

        } catch (Exception e) {

            applicationResponse.setSuccess(false);
            applicationResponse.setMessage(e.getMessage());
        }

        return applicationResponse;
    }
}
