package com.pcpl.carbon.playmobilservice.PlayMobil.SchemaMetadata.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.Model.SchemaMetadata;

import java.util.Optional;

public interface SchemaMetadataRepository extends PCPLCRUDRepository<SchemaMetadata> {
    Optional<SchemaMetadata> findBySchemaVersion(Double schemaVersion);
}
