package com.pcpl.carbon.playmobilservice.PlayMobil.SchemaMetadata.Service;

import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO.SchemaMetadataDTO;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.Model.SchemaMetadata;
import com.pcpl.carbon.playmobilservice.PlayMobil.SchemaMetadata.Repository.SchemaMetadataRepository;
import org.springframework.stereotype.Service;

@Service
public class SchemaMetadataServiceImpl extends AbstractLazyService<SchemaMetadata, SchemaMetadataDTO, SchemaMetadataRepository> implements SchemaMetadataService {
    @Override
    public SchemaMetadata getEntityObject() {
        return new SchemaMetadata();
    }

    @Override
    public SchemaMetadataDTO getDtoObject() {
        return new SchemaMetadataDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }

    @Override
    public SchemaMetadataDTO save(SchemaMetadataDTO dto) {
        return super.save(dto);
    }
}
