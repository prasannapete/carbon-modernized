package com.pcpl.carbon.authserver.User.Model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.List;

public class UserDetailDeserializer extends JsonDeserializer<UserDetail> {

    @Override
    public UserDetail deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = mapper.readTree(jsonParser);
        Long id = readJsonNode(jsonNode, "id").asLong();
        Long orgId = readJsonNode(jsonNode, "orgId").asLong();
        Long clientId = readJsonNode(jsonNode, "clientId").asLong();
        String firstName = readJsonNode(jsonNode, "firstName").asText();
        String lastName = readJsonNode(jsonNode, "lastName").asText();
        String userName = readJsonNode(jsonNode, "userName").asText();
        String password = readJsonNode(jsonNode, "password").asText();
        int accountStatus = readJsonNode(jsonNode, "accountStatus").asInt();
        int isGod = readJsonNode(jsonNode, "isGod").asInt();
        List<GrantedAuthority> authorities = mapper.readerForListOf(GrantedAuthority.class).readValue(jsonNode.get("authorities"));
        return new UserDetail(
                id,
                orgId,
                clientId,
                firstName,
                lastName,
                userName,
                password,
                accountStatus,
                isGod,
                authorities
        );
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}
