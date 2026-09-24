package com.pcpl.carbon.playmobilservice.PlayMobil.Players.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.Model.Players;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.response.PlayersResponse;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.DTO.SchemaMetadataDTO;
import com.pcpl.carbon.pcplsdk.PlayMobil.SchemaMetadata.Model.SchemaMetadata;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.Model.PlayerFeatureMapping;
import com.pcpl.carbon.playmobilservice.PlayMobil.Config.TenantSupport;
import com.pcpl.carbon.playmobilservice.PlayMobil.Players.Repository.PlayerFeatureMappingRepository;
import com.pcpl.carbon.playmobilservice.PlayMobil.PlayersCopyData.Repository.PlayersCopyDataRepository;
import com.pcpl.carbon.playmobilservice.PlayMobil.SchemaMetadata.Repository.SchemaMetadataRepository;
import com.pcpl.carbon.playmobilservice.PlayMobil.SchemaMetadata.Service.SchemaMetadataService;
import com.pcpl.carbon.playmobilservice.PlayMobil.Players.Repository.PlayersRepository;
import com.pcpl.carbon.playmobilservice.PlayMobil.User.Service.UserInformationService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.pcpl.carbon.pcplsdk.PlayMobil.PlayersCopyData.Model.PlayersCopyData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class PlayersServiceImpl implements PlayersService {
    private static final String FEATURE_CATALOG_RESOURCE = "playmobil-feature-catalog.json";
    private final SchemaMetadataRepository schemaMetadataRepository;
    private final SchemaMetadataService schemaMetadataService;
    private final PlayersRepository playersRepository;
    private final PlayerFeatureMappingRepository playerFeatureMappingRepository;
    private final UserInformationService userInformationService;
    private final PlayersCopyDataRepository playersCopyDataRepository;
    private final ObjectMapper objectMapper;
    private final DataFormatter dataFormatter = new DataFormatter();

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
            Optional<Players> optionalPlayers =
                    playersRepository.findById(schemaMetadataDTO.getPlayer().getId());

            //NEW PLAYER
            if (!optionalPlayers.isPresent()) {

                schemaMetadataDTO.getPlayer().setCreationTime(new Date());
                schemaMetadataDTO.getPlayer().setIsDeleted(0);

                Players player = playersRepository.save(TenantSupport.stampTenant(schemaMetadataDTO.getPlayer()));

                applicationResponse.setData(player);

            } else {
                //EDIT PLAYER
                Players existingPlayer = optionalPlayers.get();

                //ONLY MARK MASTER TABLE AS EDITED
                existingPlayer.setEdited(true);
                existingPlayer.setLastModifiedTime(new Date());

                playersRepository.save(existingPlayer);

                //SAVE EDITED DATA INTO COPY TABLE
                Players playerData = schemaMetadataDTO.getPlayer();

                PlayersCopyData playersCopyData = new PlayersCopyData();

                playersCopyData.setId(playerData.getId());
                playersCopyData.setOptaId(playerData.getOptaId());
                playersCopyData.setName(playerData.getName());
                playersCopyData.setShirtName(playerData.getShirtName());
                playersCopyData.setNumber(playerData.getNumber());
                playersCopyData.setPosition(playerData.getPosition());
                playersCopyData.setTeam(playerData.getTeam());
                playersCopyData.setDob(playerData.getDob());
                playersCopyData.setNationality(playerData.getNationality());
                playersCopyData.setHomeTown(playerData.getHomeTown());
                playersCopyData.setKit(playerData.getKit());

                playersCopyData.setSchemaMetadataId(schemaMetadataDTO.getId());

                playersCopyData.setFeatures(playerData.getFeatures());
                playersCopyData.setHeight(playerData.getHeight());
                playersCopyData.setWeight(playerData.getWeight());
                playersCopyData.setPreferredFoot(playerData.getPreferredFoot());
                playersCopyData.setCaptain(playerData.getCaptain());
                playersCopyData.setViceCaptain(playerData.getViceCaptain());
                playersCopyData.setCountryOfBirth(playerData.getCountryOfBirth());
                playersCopyData.setAge(playerData.getAge());

                playersCopyData.setCreatedBy(playerData.getCreatedBy());
                playersCopyData.setLastModifiedBy(playerData.getLastModifiedBy());
                playersCopyData.setDeletedBy(playerData.getDeletedBy());
                playersCopyData.setDeletedTime(playerData.getDeletedTime());

                playersCopyData.setCreationTime(existingPlayer.getCreationTime());
                playersCopyData.setLastModifiedTime(new Date());

                playersCopyData.setIsDeleted(0);
                playersCopyData.setPublished(false);
                playersCopyData.setEdited(true);

                PlayersCopyData savedCopyData = playersCopyDataRepository.save(TenantSupport.stampTenant(playersCopyData));

                applicationResponse.setData(savedCopyData);
            }
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
            User user = userInformationService.getUser();
            player.setIsDeleted(1);
            player.setDeletedBy(user.getId());
            player.setDeletedTime(new Date());
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
        User user = userInformationService.getUser();
        try{
            boolean isEditor = user.getRoles().stream()
                    .anyMatch(role ->
                            "Editor".equalsIgnoreCase(role.getName()));
            List<Players> players;

            if (!isEditor) {
                // Viewer → only published players
                players = playersRepository.findAllByIsDeletedAndIsPublishedOrderByCreationTime(0, true);

            } else {
                // Editor → all players
                List<Players> masterPlayers = playersRepository.findAllByIsDeletedOrderByCreationTime(0);

                players = new ArrayList<>();

                for (Players player : masterPlayers) {

                    Optional<PlayersCopyData> optionalPlayersCopyData = playersCopyDataRepository.findById(player.getId());

                    //IF EDITED COPY EXISTS SHOW COPY DATA TO EDITOR
                    if (optionalPlayersCopyData.isPresent()
                            && optionalPlayersCopyData.get().getIsDeleted() == 0) {

                        PlayersCopyData copyData = optionalPlayersCopyData.get();

                        Players editedPlayer = new Players();

                        editedPlayer.setId(copyData.getId());
                        editedPlayer.setOptaId(copyData.getOptaId());
                        editedPlayer.setName(copyData.getName());
                        editedPlayer.setShirtName(copyData.getShirtName());
                        editedPlayer.setNumber(copyData.getNumber());
                        editedPlayer.setPosition(copyData.getPosition());
                        editedPlayer.setTeam(copyData.getTeam());
                        editedPlayer.setDob(copyData.getDob());
                        editedPlayer.setNationality(copyData.getNationality());
                        editedPlayer.setHomeTown(copyData.getHomeTown());
                        editedPlayer.setKit(copyData.getKit());
                        editedPlayer.setSchemaMetadataId(copyData.getSchemaMetadataId());
                        editedPlayer.setFeatures(copyData.getFeatures());
                        editedPlayer.setHeight(copyData.getHeight());
                        editedPlayer.setWeight(copyData.getWeight());
                        editedPlayer.setPreferredFoot(copyData.getPreferredFoot());
                        editedPlayer.setCaptain(copyData.getCaptain());
                        editedPlayer.setViceCaptain(copyData.getViceCaptain());
                        editedPlayer.setCountryOfBirth(copyData.getCountryOfBirth());
                        editedPlayer.setAge(copyData.getAge());

                        editedPlayer.setCreatedBy(copyData.getCreatedBy());
                        editedPlayer.setCreationTime(copyData.getCreationTime());
                        editedPlayer.setLastModifiedBy(copyData.getLastModifiedBy());
                        editedPlayer.setLastModifiedTime(copyData.getLastModifiedTime());

                        editedPlayer.setIsDeleted(copyData.getIsDeleted());
                        editedPlayer.setPublished(copyData.isPublished());
                        editedPlayer.setDeletedBy(copyData.getDeletedBy());
                        editedPlayer.setDeletedTime(copyData.getDeletedTime());

                        editedPlayer.setEdited(copyData.isEdited());

                        players.add(editedPlayer);

                    } else {
                        //NO EDITED COPY SHOW MASTER DATA
                        players.add(player);
                    }
                }
            }
            applicationResponse.setPlayers(players);
            applicationResponse.setCurrentRecords(players.size());
            applicationResponse.setRecordsTotal((long) players.size());
            applicationResponse.setSuccess(true);
        }catch (Exception e){
            applicationResponse.setSuccess(false);
            applicationResponse.setMessage(e.getMessage());
        }
        return applicationResponse;
    }

    @Override
    public ApplicationResponse publishPlayersByTeamCode(String teamCode) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            if (!hasText(teamCode)) {
                applicationResponse.setSuccess(false);
                applicationResponse.setMessage("teamCode is required.");
                return applicationResponse;
            }
            List<Players> players = playersRepository.findAllByTeamIgnoreCaseAndIsDeleted(teamCode, 0);
            if (players.isEmpty()) {
                applicationResponse.setSuccess(false);
                applicationResponse.setMessage("No active players found for team code: " + teamCode);
                applicationResponse.setCurrentRecords(0);
                applicationResponse.setRecordsTotal(0L);
                return applicationResponse;
            }
            Date now = new Date();
            for (Players player : players) {

                Optional<PlayersCopyData> optionalPlayersCopyData = playersCopyDataRepository.findById(player.getId());

                // IF EDITED COPY EXISTS
                if (optionalPlayersCopyData.isPresent()) {

                    PlayersCopyData copyData = optionalPlayersCopyData.get();

                    // MOVE EDITED DATA TO MASTER TABLE
                    player.setOptaId(copyData.getOptaId());
                    player.setName(copyData.getName());
                    player.setShirtName(copyData.getShirtName());
                    player.setNumber(copyData.getNumber());
                    player.setPosition(copyData.getPosition());
                    player.setTeam(copyData.getTeam());
                    player.setDob(copyData.getDob());
                    player.setNationality(copyData.getNationality());
                    player.setHomeTown(copyData.getHomeTown());
                    player.setKit(copyData.getKit());
                    player.setSchemaMetadataId(copyData.getSchemaMetadataId());
                    player.setFeatures(copyData.getFeatures());
                    player.setHeight(copyData.getHeight());
                    player.setWeight(copyData.getWeight());
                    player.setPreferredFoot(copyData.getPreferredFoot());
                    player.setCaptain(copyData.getCaptain());
                    player.setViceCaptain(copyData.getViceCaptain());
                    player.setCountryOfBirth(copyData.getCountryOfBirth());
                    player.setAge(copyData.getAge());

                    player.setCreatedBy(copyData.getCreatedBy());
                    player.setCreationTime(copyData.getCreationTime());
                    player.setLastModifiedBy(copyData.getLastModifiedBy());
                    player.setLastModifiedTime(new Date());

                    player.setDeletedBy(copyData.getDeletedBy());
                    player.setDeletedTime(copyData.getDeletedTime());

                    player.setPublished(true);
                    player.setEdited(false);
                    copyData.setIsDeleted(1);

                    playersCopyDataRepository.save(copyData);

                } else {

                    player.setPublished(true);
                    player.setLastModifiedTime(now);
                }
            }
            List<Players> updatedPlayers = playersRepository.saveAll(players);
            Map<String, Object> result = new HashMap<>();
            result.put("teamCode", teamCode);
            result.put("updatedCount", updatedPlayers.size());
            result.put("players", updatedPlayers);
            applicationResponse.setData(result);
            applicationResponse.setCurrentRecords(updatedPlayers.size());
            applicationResponse.setRecordsTotal((long) updatedPlayers.size());
            applicationResponse.setSuccess(true);
            applicationResponse.setMessage("Players published successfully.");
        } catch (Exception e) {
            applicationResponse.setSuccess(false);
            applicationResponse.setMessage(e.getMessage());
        }
        return applicationResponse;
    }

    @Override
    public ApplicationResponse importPlayers(MultipartFile playerFile, MultipartFile catalogFile) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            if (playerFile == null || playerFile.isEmpty()) {
                applicationResponse.setSuccess(false);
                applicationResponse.setMessage("playerFile is required.");
                return applicationResponse;
            }
            int importedCatalogMappings = importCatalog(catalogFile);
            FeatureCatalog featureCatalog = loadFeatureCatalog();
            List<Players> players = readPlayers(playerFile, featureCatalog);
            if (players.isEmpty()) {
                applicationResponse.setSuccess(false);
                applicationResponse.setMessage("No player rows found in the Excel file.");
                return applicationResponse;
            }
            List<Players> savedPlayers = new ArrayList<>();
            for (Players player : players) {
                savedPlayers.add(saveImportedPlayer(player));
            }
            Map<String, Object> result = new HashMap<>();
            result.put("importedCatalogMappings", importedCatalogMappings);
            result.put("importedPlayers", savedPlayers.size());
            result.put("players", savedPlayers);
            applicationResponse.setData(result);
            applicationResponse.setCurrentRecords(savedPlayers.size());
            applicationResponse.setRecordsTotal((long) savedPlayers.size());
            applicationResponse.setSuccess(true);
            applicationResponse.setMessage("Players imported successfully.");
        } catch (Exception e) {
            applicationResponse.setSuccess(false);
            applicationResponse.setMessage("Player import failed: " + e.getMessage());
        }
        return applicationResponse;
    }

    private Players saveImportedPlayer(Players player) {
        Date now = new Date();
        Optional<Players> existingPlayer = Optional.empty();
//        if (hasText(player.getOptaId())) {
//            existingPlayer = playersRepository.findFirstByOptaIdAndIsDeleted(player.getOptaId(), 0);
//        }
        if (!existingPlayer.isPresent() && hasText(player.getId())) {
            existingPlayer = playersRepository.findById(player.getId());
        }
        if (existingPlayer.isPresent()) {
            player.setId(existingPlayer.get().getId());
            player.setCreationTime(existingPlayer.get().getCreationTime());
            player.setLastModifiedTime(now);
        } else {
            player.setId(UUID.randomUUID().toString());
            player.setCreationTime(now);
        }
        player.setIsDeleted(0);
        return playersRepository.save(TenantSupport.stampTenant(player));
    }

    private int importCatalog(MultipartFile catalogFile) throws Exception {
        if (catalogFile == null || catalogFile.isEmpty()) {
            return 0;
        }
        int savedCount = 0;
        try (Workbook workbook = WorkbookFactory.create(catalogFile.getInputStream())) {
            Map<String, String> typeIds = readFeatureTypes(workbook);
            for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
                Sheet sheet = workbook.getSheetAt(index);
                String sheetName = sheet.getSheetName();
                if (isIgnoredCatalogSheet(sheetName)) {
                    continue;
                }
                String featureTypeId = typeIds.getOrDefault(cleanKey(sheetName), featureTypeBySheet(sheetName));
                if (!hasText(featureTypeId)) {
                    continue;
                }
                savedCount += saveMappingsFromSheet(sheet, featureTypeId, sheetName);
            }
        }
        return savedCount;
    }

    private Map<String, String> readFeatureTypes(Workbook workbook) {
        Sheet sheet = workbook.getSheet("Feature Types (Internal)");
        if (sheet == null) {
            return Collections.emptyMap();
        }
        Map<String, String> typeIds = new HashMap<>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            String id = firstPart(cellText(row, 0));
            String name = cellText(row, 1);
            if (hasText(id) && hasText(name)) {
                typeIds.put(cleanKey(name), id);
            }
        }
        typeIds.putIfAbsent("skin", "FT7");
        typeIds.putIfAbsent("skintone", "FT7");
        return typeIds;
    }

    private int saveMappingsFromSheet(Sheet sheet, String featureTypeId, String sheetName) {
        int savedCount = 0;
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            String featureCode = firstFeatureCode(row);
            if (!hasText(featureCode) || "ID".equalsIgnoreCase(featureCode)) {
                continue;
            }
            String featureName = firstNameAfterCode(row, featureCode);
            String colorCode = firstColorCode(row);
            String colorName = firstNameAfterCode(row, colorCode);
            PlayerFeatureMapping mapping = playerFeatureMappingRepository
                    .findFirstByFeatureTypeIdIgnoreCaseAndFeatureCodeIgnoreCaseAndIsDeleted(featureTypeId, featureCode, 0)
                    .orElseGet(PlayerFeatureMapping::new);
            Date now = new Date();
            if (mapping.getId() == null) {
                mapping.setCreationTime(now);
            } else {
                mapping.setLastModifiedTime(now);
            }
            mapping.setFeatureTypeId(featureTypeId);
            mapping.setFeatureTypeName(sheetName);
            mapping.setFeatureCode(featureCode);
            mapping.setFeatureName(featureName);
            mapping.setColorCode(colorCode);
            mapping.setColorName(colorName);
            mapping.setIsDeleted(0);
            playerFeatureMappingRepository.save(mapping);
            savedCount++;
        }
        return savedCount;
    }

    private List<Players> readPlayers(MultipartFile playerFile, FeatureCatalog featureCatalog) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(playerFile.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return Collections.emptyList();
            }

            // Detect template layout:
            // row 0, col 1 = "VARIABLE / FIELD CODE"
            String marker = cellText(sheet.getRow(0), 1);
            if ("VARIABLE / FIELD CODE".equalsIgnoreCase(marker)) {
                return readPlayerTemplateSheet(sheet, featureCatalog);
            }

            // Otherwise assume row-based player info sheet
            return readPlayerInfoSheet(sheet, featureCatalog);
        }
    }

    private List<Players> readPlayerInfoSheet(Sheet sheet, FeatureCatalog featureCatalog) {
        Row header = sheet.getRow(0);
        if (header == null) {
            return Collections.emptyList();
        }

        Map<String, Integer> columns = new HashMap<>();
        for (int columnIndex = 0; columnIndex < header.getLastCellNum(); columnIndex++) {
            columns.put(cleanKey(cellText(header, columnIndex)), columnIndex);
        }
        List<Players> players = new ArrayList<>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null ) {
                continue;
            }
            Players player = new Players();
            player.setOptaId(value(row, columns, "Opta Player ID"));
            player.setName(value(row, columns, "Full Name"));
            player.setShirtName(value(row, columns, "Shirt Name"));
            player.setNumber(toInt(value(row, columns, "Shirt Number")));
            String positionValue = (value(row, columns, "Primary Position"));
            player.setPosition(getPositionMapping(positionValue));
            player.setDob(toLocalDate(row, columns.get(cleanKey("Date of Birth (YYYY-MM-DD)"))));
            player.setNationality(value(row, columns, "Nationality"));
            player.setHomeTown(value(row, columns, "City of Birth"));
            player.setCountryOfBirth(value(row, columns, "Country of Birth"));
            player.setTeam(upper(value(row, columns, "Team")));
            player.setKit(upper(value(row, columns, "Kit code")));
            Integer parsedAge = toInt(value(row, columns, "Age"));
            int age = parsedAge == null ? calculateAge(player.getDob()) : parsedAge;
            player.setAge(age);
            player.setHeight(toDouble(value(row,columns,"Height (cm)"), 0.0));
            player.setWeight(toDouble(value(row,columns,"Weight (kg)"), 0.0));
            String preferredFoot = upper(value(row, columns, "Preferred Foot"));
            player.setPreferredFoot("RIGHT".equals(preferredFoot) || "R".equals(preferredFoot) ? 0 : 1);
            boolean captain = isYes(value(row, columns, "Captain"));
            boolean viceCaptain = isYes(value(row, columns, "Vice Captain")) && !captain;
            player.setCaptain(captain);
            player.setViceCaptain(viceCaptain);
            ArrayNode features = objectMapper.createArrayNode();
            putFeatureWithCatalog(features, featureCatalog, "FT1", upper(value(row, columns, "Head Art Code")), upper(value(row, columns, "Head Art Colour")));
            putFeatureWithCatalog(features, featureCatalog, "FT2", upper(value(row, columns, "Eyebrows Code")), upper(value(row, columns, "Eyebrows Colour Code")));
            putFeatureWithCatalog(features, featureCatalog, "FT3", upper(value(row, columns, "Eye Colour Code")), null);
            putFeatureWithCatalog(features, featureCatalog, "FT4", upper(value(row, columns, "Moustache Code")), upper(value(row, columns, "Moustache Colour Code")));
            putFeatureWithCatalog(features, featureCatalog, "FT5", upper(value(row, columns, "Lip Code")), upper(value(row, columns, "Lip Colour Code")));
            putFeatureWithCatalog(features, featureCatalog, "FT6", upper(value(row, columns, "Beard Code")), upper(value(row, columns, "Beard Colour Code")));
            putFeatureWithCatalog(features, featureCatalog, "FT7", upper(value(row, columns, "Skin Tone Code")), null);
            player.setFeatures(features);
            players.add(player);
        }
        return players;
    }

    private List<Players> readPlayerTemplateSheet(Sheet sheet, FeatureCatalog featureCatalog) {
        if (sheet == null) {
            return Collections.emptyList();
        }

        Map<String, Row> codeRows = new HashMap<>();
        Row playerHeader = null;

        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }

            String fieldName = cellText(row, 1); // column B
            if ("VARIABLE / FIELD CODE".equalsIgnoreCase(fieldName)) {
                playerHeader = row;
                continue;
            }

            if (hasText(fieldName)) {
                codeRows.put(cleanKey(fieldName), row);
            }
        }

        if (playerHeader == null) {
            return Collections.emptyList();
        }

        List<Players> players = new ArrayList<>();

        for (int columnIndex = 2; columnIndex < playerHeader.getLastCellNum(); columnIndex++) {
            if (!hasText(cellText(playerHeader, columnIndex))) {
                continue;
            }

            String name = fieldValue(codeRows, "Full Name", columnIndex);
            if (!hasText(name)) {
                continue;
            }

            Players player = new Players();
            player.setName(name);
            player.setShirtName(fieldValue(codeRows, "Shirt Name", columnIndex));
            player.setNumber(toInt(fieldValue(codeRows, "Shirt Number", columnIndex)));
            player.setOptaId(fieldValue(codeRows, "Opta Player ID", columnIndex));
            player.setDob(toLocalDate(codeRows.get(cleanKey("Date of Birth (YYYY-MM-DD)")), columnIndex));
            player.setNationality(fieldValue(codeRows, "Nationality", columnIndex));
            player.setHomeTown(fieldValue(codeRows, "City of Birth", columnIndex));
            player.setCountryOfBirth(fieldValue(codeRows, "Country of Birth", columnIndex));
            String positionValue = (fieldValue(codeRows, "Primary Position", columnIndex));
            player.setPosition(getPositionMapping(positionValue));
            player.setTeam(upper(fieldValue(codeRows, "Team", columnIndex)));
            player.setKit(upper(fieldValue(codeRows, "Kit code", columnIndex)));
            int age =  (fieldValue(codeRows, "Age", columnIndex)==null ? calculateAge(player.getDob()):(toInt(fieldValue(codeRows, "Age", columnIndex))));
            player.setAge(age);
            player.setHeight(toDouble(fieldValue(codeRows,"Height (cm)",columnIndex), 0.0));
            player.setWeight(toDouble(fieldValue(codeRows,"Weight (kg)",columnIndex), 0.0));
            String preferredFoot = upper(fieldValue(codeRows, "Preferred Foot", columnIndex));
            player.setPreferredFoot("RIGHT".equals(preferredFoot) || "R".equals(preferredFoot) ? 0 : 1);
            boolean captain = isYes(fieldValue(codeRows, "Captain", columnIndex));
            boolean viceCaptain = isYes(fieldValue(codeRows, "Vice Captain", columnIndex)) && !captain;
            player.setCaptain(captain);
            player.setViceCaptain(viceCaptain);
            ArrayNode features = objectMapper.createArrayNode();
            putFeatureWithCatalog(features, featureCatalog, "FT1",
                    upper(fieldValue(codeRows, "Head Art Code", columnIndex)),
                    upper(fieldValue(codeRows, "Head Art Colour", columnIndex)));
            putFeatureWithCatalog(features, featureCatalog, "FT2",
                    upper(fieldValue(codeRows, "Eyebrows Code", columnIndex)),
                    upper(fieldValue(codeRows, "Eyebrows Colour Code", columnIndex)));
            putFeatureWithCatalog(features, featureCatalog, "FT3",
                    upper(fieldValue(codeRows, "Eye Colour Code", columnIndex)),null
                    );
            putFeatureWithCatalog(features, featureCatalog, "FT4",
                    upper(fieldValue(codeRows, "Moustache Code", columnIndex)),
                    upper(fieldValue(codeRows, "Moustache Colour Code", columnIndex)));
            putFeatureWithCatalog(features, featureCatalog, "FT5",
                    upper(fieldValue(codeRows, "Lip Code", columnIndex)),
                    upper(fieldValue(codeRows, "Lip Colour Code", columnIndex)));
            putFeatureWithCatalog(features, featureCatalog, "FT6",
                    upper(fieldValue(codeRows, "Beard Code", columnIndex)),
                    upper(fieldValue(codeRows, "Beard Colour Code", columnIndex)));
            putFeatureWithCatalog(features, featureCatalog, "FT7",
                    upper(fieldValue(codeRows, "Skin Tone Code", columnIndex)),
                    null);

            player.setFeatures(features);
            players.add(player);
        }

        return players;
    }

    private String fieldValue(Map<String, Row> codeRows, String fieldName, int columnIndex) {
        Row row = codeRows.get(cleanKey(fieldName));
        return row == null ? null : cellText(row, columnIndex);
    }

    private String cleanKey(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    private FeatureCatalog loadFeatureCatalog() {
        FeatureCatalog resourceCatalog = loadFeatureCatalogFromResource();
        if (!resourceCatalog.isEmpty()) {
            return resourceCatalog;
        }

        List<PlayerFeatureMapping> mappings = playerFeatureMappingRepository.findAllByIsDeletedOrderByIdAsc(0);
        if (mappings == null || mappings.isEmpty()) {
            return FeatureCatalog.empty();
        }

        Map<String, FeatureDefaults> defaultsByType = new HashMap<>();
        Map<String, Map<String, String>> colorsByTypeAndFeature = new HashMap<>();

        for (PlayerFeatureMapping mapping : mappings) {
            String type = normalizeFeatureKey(mapping.getFeatureTypeId());
            String featureCode = normalizeFeatureKey(mapping.getFeatureCode());
            String colorCode = normalizeFeatureKey(mapping.getColorCode());
            if (!hasText(type) || !hasText(featureCode)) {
                continue;
            }

            Map<String, String> colorByFeature = colorsByTypeAndFeature.computeIfAbsent(type, key -> new HashMap<>());
            if (!hasText(colorByFeature.get(featureCode))) {
                colorByFeature.put(featureCode, colorCode);
            }

            FeatureDefaults existingDefault = defaultsByType.get(type);
            boolean mappingMarkedDefault = isDefaultMapping(mapping);
            if (existingDefault == null || (mappingMarkedDefault && !existingDefault.explicitDefault)) {
                defaultsByType.put(type, new FeatureDefaults(featureCode, colorCode, mappingMarkedDefault));
            }
        }

        return new FeatureCatalog(defaultsByType, colorsByTypeAndFeature);
    }

    private FeatureCatalog loadFeatureCatalogFromResource() {
        try (InputStream stream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(FEATURE_CATALOG_RESOURCE)) {
            if (stream == null) {
                return FeatureCatalog.empty();
            }

            Map<String, FeatureDefaults> defaultsByType = new HashMap<>();
            Map<String, Map<String, String>> colorsByTypeAndFeature = new HashMap<>();

            for (var typeNode : objectMapper.readTree(stream).path("types")) {
                String featureTypeId = normalizeFeatureKey(typeNode.path("featureTypeId").asText(null));
                if (!hasText(featureTypeId)) {
                    continue;
                }

                String defaultFeatureCode = normalizeFeatureKey(typeNode.path("defaultFeatureCode").asText(null));
                String defaultColorCode = normalizeFeatureKey(typeNode.path("defaultColorCode").asText(null));
                if (hasText(defaultFeatureCode)) {
                    defaultsByType.put(featureTypeId, new FeatureDefaults(defaultFeatureCode, defaultColorCode, true));
                }

                Map<String, String> colorByFeature = colorsByTypeAndFeature.computeIfAbsent(featureTypeId, key -> new HashMap<>());
                for (var mappingNode : typeNode.path("mappings")) {
                    String featureCode = normalizeFeatureKey(mappingNode.path("featureCode").asText(null));
                    if (!hasText(featureCode)) {
                        continue;
                    }
                    String colorCode = normalizeFeatureKey(mappingNode.path("colorCode").asText(null));
                    if (!colorByFeature.containsKey(featureCode)) {
                        colorByFeature.put(featureCode, colorCode);
                    }
                }
            }

            return new FeatureCatalog(defaultsByType, colorsByTypeAndFeature);
        } catch (Exception ignored) {
            return FeatureCatalog.empty();
        }
    }

    private boolean isDefaultMapping(PlayerFeatureMapping mapping) {
        return containsDefaultText(mapping.getFeatureName()) || containsDefaultText(mapping.getColorName());
    }

    private boolean containsDefaultText(String text) {
        String normalized = cleanKey(text);
        return hasText(normalized) && normalized.contains("default");
    }

    private String normalizeFeatureKey(String value) {
        String cleaned = cleanText(value);
        return cleaned == null ? null : cleaned.toUpperCase(Locale.ROOT);
    }

    private void putFeatureByCode(ArrayNode features, String code, String colorCode) {
        String cleanCode = cleanText(code);
        if (!hasText(cleanCode)) {
            return;
        }
        String featureTypeId = featureTypeByCode(cleanCode);
        putFeature(features, featureTypeId, cleanCode, colorCode);
    }

    private void putFeatureWithCatalog(ArrayNode features, FeatureCatalog featureCatalog, String featureTypeId, String code, String colorCode) {
        String normalizedType = normalizeFeatureKey(featureTypeId);
        String normalizedCode = normalizeFeatureKey(code);
        String normalizedColorCode = normalizeFeatureKey(colorCode);

        if (hasText(normalizedCode) && normalizedCode.contains("#")) {
            String[] parts = normalizedCode.split("#");
            normalizedCode = normalizeFeatureKey(parts[0]);
            if (parts.length > 1 && !hasText(normalizedColorCode)) {
                normalizedColorCode = normalizeFeatureKey(parts[1]);
            }
        }

        FeatureDefaults defaults = featureCatalog.defaultFor(normalizedType);
        Map<String, String> colorsByFeature = featureCatalog.colorsFor(normalizedType);
        boolean hasCatalogForType = defaults != null || !colorsByFeature.isEmpty();

        String finalCode = normalizedCode;
        boolean usedDefaultFallback = false;
        if (hasCatalogForType && (!hasText(finalCode) || !colorsByFeature.containsKey(finalCode))) {
            finalCode = defaults == null ? null : defaults.featureCode;
            usedDefaultFallback = true;
        }

        String finalColor = normalizedColorCode;
        if (usedDefaultFallback) {
            finalColor = defaults == null ? null : defaults.colorCode;
            if (!hasText(finalColor)) {
                String catalogColor = colorsByFeature.get(finalCode);
                finalColor = hasText(catalogColor) ? catalogColor : null;
            }
        } else if (!hasText(finalColor)) {
            String catalogColor = colorsByFeature.get(finalCode);
            finalColor = hasText(catalogColor) ? catalogColor : (defaults == null ? null : defaults.colorCode);
        }

        putFeature(features, normalizedType, finalCode, finalColor);
    }

    private void putFeature(ArrayNode features, String featureTypeId, String code, String colorCode) {
        String cleanCode = cleanText(code);
        String cleanColorCode = cleanText(colorCode);
        if (!hasText(cleanCode) || !hasText(featureTypeId)) {
            return;
        }
        if (cleanCode.contains("#")) {
            String[] parts = cleanCode.split("#");
            cleanCode = parts[0].trim();
            if (parts.length > 1 && !hasText(cleanColorCode)) {
                cleanColorCode = parts[1].trim();
            }
        }
        String value = hasText(cleanColorCode) ? cleanCode + "#" + cleanColorCode : cleanCode;
        ObjectNode featureNode = objectMapper.createObjectNode();
        featureNode.put("type", featureTypeId);
        featureNode.put("feature", value);
        features.add(featureNode);
    }

    private String featureTypeByCode(String code) {
        String cleanCode = cleanText(code);
        if (!hasText(cleanCode)) {
            return null;
        }
        if (cleanCode.contains("#")) {
            cleanCode = cleanCode.substring(0, cleanCode.indexOf("#"));
        }
        String upperCode = cleanCode.toUpperCase(Locale.ROOT);
        if (upperCode.startsWith("EB")) {
            return "FT2";
        }
        if (upperCode.startsWith("H")) {
            return "FT1";
        }
        if (upperCode.startsWith("E")) {
            return "FT3";
        }
        if (upperCode.startsWith("M")) {
            return "FT4";
        }
        if (upperCode.startsWith("L")) {
            return "FT5";
        }
        if (upperCode.startsWith("B")) {
            return "FT6";
        }
        if (upperCode.startsWith("S") || upperCode.startsWith("C")) {
            return "FT7";
        }
        return null;
    }

    private String featureTypeBySheet(String sheetName) {
        String key = cleanKey(sheetName);
        if ("hair".equals(key)) {
            return "FT1";
        }
        if ("eyebrows".equals(key)) {
            return "FT2";
        }
        if ("eyes".equals(key)) {
            return "FT3";
        }
        if ("moustache".equals(key)) {
            return "FT4";
        }
        if ("lips".equals(key)) {
            return "FT5";
        }
        if ("beard".equals(key)) {
            return "FT6";
        }
        if ("skin".equals(key) || "skintone".equals(key)) {
            return "FT7";
        }
        return null;
    }

    private boolean isIgnoredCatalogSheet(String sheetName) {
        String key = cleanKey(sheetName);
        return Arrays.asList("playerinfo", "teams", "kits", "featuretypesinternal").contains(key);
    }

    private String firstFeatureCode(Row row) {
        for (int columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
            String text = cellText(row, columnIndex);
            if (isFeatureCode(text)) {
                return text;
            }
        }
        return null;
    }

    private String firstColorCode(Row row) {
        for (int columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
            String text = cellText(row, columnIndex);
            if (hasText(text) && text.toUpperCase(Locale.ROOT).startsWith("C") && text.length() > 1) {
                return text;
            }
        }
        return null;
    }

    private String firstNameAfterCode(Row row, String code) {
        if (!hasText(code)) {
            return null;
        }
        for (int columnIndex = 0; columnIndex < row.getLastCellNum() - 1; columnIndex++) {
            if (code.equalsIgnoreCase(cellText(row, columnIndex))) {
                return cellText(row, columnIndex + 1);
            }
        }
        return null;
    }

    private boolean isFeatureCode(String text) {
        if (!hasText(text) || "ID".equalsIgnoreCase(text)) {
            return false;
        }
        String upperText = text.toUpperCase(Locale.ROOT);
        return upperText.matches("(H|EB|E|M|L|B|S)[A-Z0-9]+");
    }

    private String fieldCode(String text) {
        if (!hasText(text)) {
            return null;
        }
        Matcher matcher = Pattern.compile("\\[([^]]+)]").matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }


    private String value(Row row, Map<String, Integer> columns, String key) {
        Integer columnIndex = columns.get(cleanKey(key));
        return cellText(row, columnIndex);
    }
    private int getPositionMapping(String positionValue){
        String normalizedPosition = upper(positionValue);
        int postition = 0;
        switch (normalizedPosition){
            case "FWD": postition = 0;
                        break;
            case "MID": postition = 1;
                        break;
            case "DEF": postition = 2;
                        break;
            case "GK": postition = 3;
                        break;
        }
        return postition;
    }
    private LocalDate toLocalDate(Row row, Integer columnIndex) {
        if (row == null || columnIndex == null || columnIndex < 0) {
            return null;
        }

        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }

        try {
            CellType cellType = cell.getCellType();
            if (cellType == CellType.FORMULA) {
                cellType = cell.getCachedFormulaResultType();
            }

            if (cellType == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate();
                }

                double numericValue = cell.getNumericCellValue();
                return DateUtil.getJavaDate(numericValue)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            if (cellType == CellType.STRING) {
                String text = cell.getStringCellValue();
                if (text == null) {
                    return null;
                }

                String normalized = text.trim();
                if (normalized.isEmpty() || "YYYY-MM-DD".equalsIgnoreCase(normalized)) {
                    return null;
                }

                if (normalized.matches("\\d+(\\.\\d+)?")) {
                    double numericValue = Double.parseDouble(normalized);
                    return DateUtil.getJavaDate(numericValue)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                }

                int timeSepIndex = normalized.indexOf('T');
                if (timeSepIndex > 0) {
                    normalized = normalized.substring(0, timeSepIndex).trim();
                } else {
                    int spaceIndex = normalized.indexOf(' ');
                    if (spaceIndex > 0) {
                        normalized = normalized.substring(0, spaceIndex).trim();
                    }
                }

                try {
                    return LocalDate.parse(normalized);
                } catch (DateTimeParseException ignored) {
                }

                for (DateTimeFormatter formatter : supportedDobFormatters()) {
                    try {
                        return LocalDate.parse(normalized, formatter);
                    } catch (DateTimeParseException ignored) {
                    }
                }

                for (DateTimeFormatter formatter : supportedDobDateTimeFormatters()) {
                    try {
                        return LocalDateTime.parse(text.trim(), formatter).toLocalDate();
                    } catch (DateTimeParseException ignored) {
                    }
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private List<DateTimeFormatter> supportedDobFormatters() {
        DateTimeFormatter strictDmySlash = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("d/M/uuuu")
                .toFormatter(Locale.ROOT)
                .withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter strictMdySlash = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("M/d/uuuu")
                .toFormatter(Locale.ROOT)
                .withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter strictDmyDash = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("d-M-uuuu")
                .toFormatter(Locale.ROOT)
                .withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter strictDmyDash2 = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd-MM-uuuu")
                .toFormatter(Locale.ROOT)
                .withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter strictDmySlash2 = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd/MM/uuuu")
                .toFormatter(Locale.ROOT)
                .withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter strictDMonY = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("d-MMM-uuuu")
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter strictDMonY2 = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("d-MMM-uu")
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
        return Arrays.asList(
                strictDmySlash,
                strictMdySlash,
                strictDmyDash,
                strictDmyDash2,
                strictDmySlash2,
                strictDMonY,
                strictDMonY2
        );
    }

    private List<DateTimeFormatter> supportedDobDateTimeFormatters() {
        DateTimeFormatter isoDateTime = DateTimeFormatter.ISO_DATE_TIME;
        DateTimeFormatter spaceDateTime = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("uuuu-MM-dd HH:mm:ss")
                .toFormatter(Locale.ROOT)
                .withResolverStyle(ResolverStyle.STRICT);
        return Arrays.asList(isoDateTime, spaceDateTime);
    }

    private Integer toInt(String text) {
        try {
            if (!hasText(text)) {
                return null;
            }
            return Double.valueOf(text.trim()).intValue();
        } catch (Exception e) {
            return null;
        }
    }

    private double toDouble(String text, double defaultValue) {
        try {
            if (!hasText(text)) {
                return defaultValue;
            }
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String cellText(Row row, Integer columnIndex) {
        if (row == null || columnIndex == null || columnIndex < 0) {
            return null;
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }
        return cleanText(dataFormatter.formatCellValue(cell));
    }

    private String firstPart(String text) {
        if (!hasText(text)) {
            return null;
        }
        return text.trim().split("\\s+")[0];
    }

    private String firstText(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String cleanText(String text) {
        if (text == null) {
            return null;
        }
        String cleanText = text.trim();
        return cleanText.isEmpty() ? null : cleanText;
    }

    private String upper(String text) {
        String cleaned = cleanText(text);
        return cleaned == null ? null : cleaned.toUpperCase(Locale.ROOT);
    }

    private boolean isYes(String text) {
        return "Y".equals(upper(text));
    }

    private boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    private static class FeatureCatalog {
        private final Map<String, FeatureDefaults> defaultsByType;
        private final Map<String, Map<String, String>> colorsByTypeAndFeature;

        private FeatureCatalog(Map<String, FeatureDefaults> defaultsByType, Map<String, Map<String, String>> colorsByTypeAndFeature) {
            this.defaultsByType = defaultsByType;
            this.colorsByTypeAndFeature = colorsByTypeAndFeature;
        }

        private static FeatureCatalog empty() {
            return new FeatureCatalog(Collections.emptyMap(), Collections.emptyMap());
        }

        private boolean isEmpty() {
            return defaultsByType.isEmpty() && colorsByTypeAndFeature.isEmpty();
        }

        private FeatureDefaults defaultFor(String featureTypeId) {
            return defaultsByType.get(featureTypeId);
        }

        private Map<String, String> colorsFor(String featureTypeId) {
            return colorsByTypeAndFeature.getOrDefault(featureTypeId, Collections.emptyMap());
        }
    }

    private static class FeatureDefaults {
        private final String featureCode;
        private final String colorCode;
        private final boolean explicitDefault;

        private FeatureDefaults(String featureCode, String colorCode, boolean explicitDefault) {
            this.featureCode = featureCode;
            this.colorCode = colorCode;
            this.explicitDefault = explicitDefault;
        }
    }

    public int calculateAge(LocalDate dob) {
        if (dob == null) {
            return 0;
        }
        return Period.between(dob, LocalDate.now()).getYears();
    }
}
