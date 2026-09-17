package com.pcpl.carbon.shell_analytics_service.PlayMobil.SchemaMetadata.Controller;

import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO.SchemaMetadataDTO;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.Model.SchemaMetadata;
import com.pcpl.carbon.shell_analytics_service.PlayMobil.SchemaMetadata.Repository.SchemaMetadataRepository;
import com.pcpl.carbon.shell_analytics_service.PlayMobil.SchemaMetadata.Service.SchemaMetadataServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/play-mobil/schema-metadata")
public class SchemaMetadataController extends AbstractCRUDController<SchemaMetadata, SchemaMetadataDTO, SchemaMetadataRepository, SchemaMetadataServiceImpl> {
}
