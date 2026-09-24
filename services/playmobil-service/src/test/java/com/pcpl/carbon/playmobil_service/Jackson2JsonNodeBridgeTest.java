package com.pcpl.carbon.playmobil_service;

import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO.SchemaMetadataDTO;
import com.pcpl.carbon.playmobilservice.PlayMobil.Config.Jackson2JsonNodeBridgeConfig;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves the Jackson 3 (Boot 4) HTTP mapper can bind the pcpl-sdk's Jackson 2 JsonNode field
 * ({@code Players.features}) once the bridge module is registered — the exact path that was
 * throwing HTTP 500 on POST /players/save-data.
 */
class Jackson2JsonNodeBridgeTest {

    private static final String PAYLOAD = """
            {
              "schemaVersion": 1.0,
              "player": {
                "id": "1001",
                "optaId": "OPTA1001",
                "name": "Virat Kumar",
                "position": 0,
                "preferredFoot": 1,
                "features": "Speed, Dribbling, Finishing",
                "createdBy": 1,
                "lastModifiedBy": 1
              }
            }
            """;

    private JsonMapper mapperWithBridge() {
        JacksonModule bridge = new Jackson2JsonNodeBridgeConfig().jackson2JsonNodeBridgeModule();
        // Mirror Boot 4 config: bridge module + the restored pre-migration primitive behaviour.
        return JsonMapper.builder()
                .addModules(List.of(bridge))
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .build();
    }

    @Test
    void bindsJackson2JsonNodeField_andRoundTrips() {
        JsonMapper mapper = mapperWithBridge();

        // Deserialize (the request-binding path that used to 500)
        SchemaMetadataDTO dto = mapper.readValue(PAYLOAD, SchemaMetadataDTO.class);
        assertNotNull(dto.getPlayer(), "player should bind");
        assertNotNull(dto.getPlayer().getFeatures(), "features (Jackson 2 JsonNode) should bind");
        assertEquals("Speed, Dribbling, Finishing", dto.getPlayer().getFeatures().asText());

        // Serialize back (the response path)
        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"features\":\"Speed, Dribbling, Finishing\""),
                "features should serialize back as raw JSON, was: " + json);
    }
}
