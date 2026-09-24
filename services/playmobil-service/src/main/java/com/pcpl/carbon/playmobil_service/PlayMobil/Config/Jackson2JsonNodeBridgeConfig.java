package com.pcpl.carbon.playmobilservice.PlayMobil.Config;

import com.fasterxml.jackson.databind.JsonNode;                 // Jackson 2 (used by pcpl-sdk)
import com.fasterxml.jackson.databind.ObjectMapper;             // Jackson 2
import com.fasterxml.jackson.databind.node.NullNode;            // Jackson 2

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.core.JacksonException;                    // Jackson 3 (Spring Boot 4 HTTP layer)
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.io.IOException;

/**
 * Spring Boot 4's HTTP message converters run on Jackson 3 ({@code tools.jackson.*}), but the
 * shared pcpl-sdk models/DTOs declare fields of the Jackson 2 type
 * {@link com.fasterxml.jackson.databind.JsonNode} (e.g. {@code Players.features}). Jackson 3
 * cannot construct that abstract Jackson 2 type, so binding such a request body fails with
 * "Cannot construct instance of com.fasterxml.jackson.databind.JsonNode" (HTTP 500), and the
 * same type cannot be written back on responses.
 *
 * This module bridges the two Jackson generations for that one type: it reads the incoming
 * subtree with the Jackson 3 parser and re-parses its raw JSON into a Jackson 2 JsonNode, and
 * writes a Jackson 2 JsonNode back out as raw JSON. Boot 4's JacksonAutoConfiguration collects
 * every {@link JacksonModule} bean and registers it on the auto-configured mapper, so exposing
 * this as a bean is enough.
 */
@Configuration
public class Jackson2JsonNodeBridgeConfig {

    // Only used to (re)parse a raw JSON string into the Jackson 2 node tree.
    private static final ObjectMapper JACKSON2 = new ObjectMapper();

    @Bean
    public JacksonModule jackson2JsonNodeBridgeModule() {
        SimpleModule module = new SimpleModule("Jackson2JsonNodeBridge");
        module.addDeserializer(JsonNode.class, new Jackson2JsonNodeDeserializer());
        module.addSerializer(JsonNode.class, new Jackson2JsonNodeSerializer());
        return module;
    }

    private static final class Jackson2JsonNodeDeserializer extends ValueDeserializer<JsonNode> {
        @Override
        public JsonNode deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
            tools.jackson.databind.JsonNode node = context.readTree(parser);
            if (node == null || node.isNull()) {
                return NullNode.getInstance();
            }
            try {
                return JACKSON2.readTree(node.toString());
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to convert JSON into a Jackson 2 JsonNode", e);
            }
        }
    }

    private static final class Jackson2JsonNodeSerializer extends ValueSerializer<JsonNode> {
        @Override
        public void serialize(JsonNode value, JsonGenerator generator, SerializationContext context) throws JacksonException {
            if (value == null || value.isNull()) {
                generator.writeNull();
                return;
            }
            // A Jackson 2 node's toString() is valid JSON; write it straight through.
            generator.writeRawValue(value.toString());
        }
    }
}
