package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.StorageObject;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.*;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.Country;
import com.pcpl.carbon.shell_analytics_service.PlayStoreReport.Service.PlayStoreReportServiceImpl;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Service.AppLaunchedService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppLaunched.Service.AppLaunchedServiceImpl;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.AppStoreSalesReport.Service.AppStoreSalesServiceImpl;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.BrandViewedGarage.Service.BrandViewedGarageService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.BrandViewedGarage.Service.BrandViewedGarageServiceImpl;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CarUnlocked.Service.CarUnlockedService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CarUnlocked.Service.CarUnlockedServiceImpl;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Country.Repository.CountryRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RCPlayed.Service.RCPlayedService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RCPlayed.Service.RCPlayedServiceImpl;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RacePlayed.Service.RacePlayedService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.RacePlayed.Service.RacePlayedServiceImpl;
import com.pcpl.carbon.shell_analytics_service.config.SrlAnalyticsConfig;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class ShellEventsServiceImpl implements ShellEventsService{
   @Autowired
   RedisTemplate<String, String> redisTemplate;

   @Autowired
   AppLaunchedServiceImpl appLaunchedServiceImpl;

   @Autowired
    CarUnlockedServiceImpl carUnlockedServiceImpl;

   @Autowired
   RacePlayedServiceImpl racePlayedServiceImpl;

   @Autowired
    RCPlayedServiceImpl rcPlayedServiceImpl;

   @Autowired
    BrandViewedGarageServiceImpl brandViewedGarageServiceImpl;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
   ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private SrlAnalyticsConfig srlAnalyticsConfig;

    @Autowired
    AppStoreSalesServiceImpl appStoreSalesServiceImpl;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    private AppLaunchedService appLaunchedService;

    @Autowired
    private BrandViewedGarageService brandViewedGarageService;

    @Autowired
    private CarUnlockedService carUnlockedService;

    @Autowired
    private RCPlayedService rcPlayedService;

    @Autowired
    private RacePlayedService racePlayedService;

    @Autowired
    PlayStoreReportServiceImpl playStoreReportServiceImpl;

    public void RedisObjectService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public ApplicationResponse saveObject(String key, ShellEventsDataDTO shellEventsDataDTO) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            String jsonString = objectMapper.writeValueAsString(shellEventsDataDTO);
            ListOperations<String, String> ops = redisTemplate.opsForList();
            ops.rightPush(key,jsonString);
            applicationResponse.setSuccess(true);
            applicationResponse.setMessage("Object saved successfully.");
            applicationResponse.setError(null);
        } catch (JsonProcessingException e) {
            applicationResponse.setError("Failed to save object."+e.getMessage());
            applicationResponse.setSuccess(false);

            throw new RuntimeException("Failed to serialize object", e);

        }
        return applicationResponse;
    }

    @Override
    public ApplicationResponse getObject(String key) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        ListOperations<String, String> ops = redisTemplate.opsForList();
        List<String> jsonStrings = ops.range("shell_events", 0, 4);
        try {
            List<ShellEventsDataDTO> shellEventsList = new ArrayList<>();
            for (String json : jsonStrings) {
                ShellEventsDataDTO shellEvent = objectMapper.readValue(json, ShellEventsDataDTO.class);
                shellEventsList.add(shellEvent);
            }
            applicationResponse.setData(shellEventsList);
            applicationResponse.setSuccess(true);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize object", e);
        }
        return applicationResponse;
    }

    @Override
    public ApplicationResponse saveShellEvents(ShellEventsDataDTO shellEventsDataDTO) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            switch (shellEventsDataDTO.getEventType()){
            case 1:
                AppLaunchedDTO appLaunchedDTO = objectMapper.convertValue(shellEventsDataDTO.getShellEvents(), AppLaunchedDTO.class);
                applicationResponse.setMessage("AppLaunched event saved successfully!");
                applicationResponse.setData(appLaunchedServiceImpl.save(appLaunchedDTO));
                applicationResponse.setSuccess(true);
                break;
            case 2:
                CarUnlockedDTO carUnlockedDTO = objectMapper.convertValue(shellEventsDataDTO.getShellEvents(), CarUnlockedDTO.class);
                applicationResponse.setMessage("Car unlocked event saved successfully!");
                applicationResponse.setData(carUnlockedServiceImpl.save(carUnlockedDTO));
                applicationResponse.setSuccess(true);
                break;
            case 3:
                RacePlayedDTO racePlayedDTO = objectMapper.convertValue(shellEventsDataDTO.getShellEvents(), RacePlayedDTO.class);
                applicationResponse.setMessage("Car unlocked event saved successfully!");
                applicationResponse.setData(racePlayedServiceImpl.save(racePlayedDTO));
                applicationResponse.setSuccess(true);
                break;
            case 4:
                RCPlayedDTO rcPlayedDTO = objectMapper.convertValue(shellEventsDataDTO.getShellEvents(), RCPlayedDTO.class);
                applicationResponse.setMessage("RC played event saved successfully!");
                applicationResponse.setData(rcPlayedServiceImpl.save(rcPlayedDTO));
                applicationResponse.setSuccess(true);
                break;
            case 5:
                BrandViewedGarageDTO brandViewedGarageDTO = objectMapper.convertValue(shellEventsDataDTO.getShellEvents(), BrandViewedGarageDTO.class);
                applicationResponse.setMessage("Brand viewed garage event saved successfully!");
                applicationResponse.setData(brandViewedGarageServiceImpl.save(brandViewedGarageDTO));
                applicationResponse.setSuccess(true);
                break;
            default:
                applicationResponse.setMessage("The type caste is not working");
                applicationResponse.setSuccess(false);
                break;
        }
        }catch (Exception e){
            log.error(e.getMessage());
            applicationResponse.setError(e.getMessage());
            applicationResponse.setSuccess(false);
        }
        return applicationResponse;
    }

    @Override
    public ApplicationResponse getAppId(String appId, String jwtToken) throws Exception {
        log.trace("Entering");
        ApplicationResponse reviewsResponse = ApplicationResponse.builder().build();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtToken);

            String url = "https://api.appstoreconnect.apple.com/v1/apps/" + appId + "/appStoreVersions?limit=200";

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ApplicationResponse> result =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            ApplicationResponse.class
                    );

            reviewsResponse = result.getBody();
            log.trace("Completed Successfully");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        }
        log.trace("Exiting");
        return reviewsResponse;
    }

    @Override
    public ApplicationResponse fetchSaleReport(String jwtToken,String date) throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        String baseUrl = "https://api.appstoreconnect.apple.com/v1/salesReports";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());     // set to current date
        calendar.add(Calendar.DATE, -2);  // subtract 1 day
        Date yesterday = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(date != null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date reportDate = sdf.parse(date);
            yesterday = reportDate;
        }
        String formattedDate = sdf.format(yesterday);
        String fullUrl = baseUrl +
                "?filter%5BreportDate%5D=" + URLEncoder.encode(formattedDate, StandardCharsets.UTF_8) +
                "&filter%5BreportType%5D=SALES" +
                "&filter%5BreportSubType%5D=SUMMARY" +
                "&filter%5Bfrequency%5D=DAILY" +
                "&filter%5BvendorNumber%5D=" + URLEncoder.encode(srlAnalyticsConfig.getVendorNumber(), StandardCharsets.UTF_8) ;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Authorization", "Bearer " + jwtToken)
                .header("Accept", "application/a-gzip")
                .GET()
                .build();

        HttpResponse<byte []> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        log.trace(response.body().toString());
        if (response.statusCode() == 200) {
            extractReport(response.body(), "sales-report.tsv");
           List<AppStoreSalesReportDTO> appStoreSalesReportDTOS =  parseTsv(Paths.get("sales-report.tsv").toFile());
           applicationResponse.setData(appStoreSalesReportDTOS);
           applicationResponse.setMessage("Data saved successfully!");
           applicationResponse.setSuccess(true);
        } else {
            applicationResponse.setSuccess(false);
            applicationResponse.setMessage("Error: " + response.statusCode());
            log.trace("Error: " + response.statusCode());
            log.trace("Error: " + response.previousResponse());
        }
        return applicationResponse;
    }

    @Override
    public ApplicationResponse playStoreReport() throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
         String serviceAccountFile = srlAnalyticsConfig.getPlaystoreJsonFilePath();
         String BUCKET_NAME = "pubsite_prod_6863089700952710604";
         String OBJECT_NAME = "stats_installs_installs_com.TDF.ShellRacingLegends_202507_country.csv";

            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            GoogleCredential credential = GoogleCredential
                    .fromStream(new FileInputStream(serviceAccountFile))
                    .createScoped(Collections.singleton(StorageScopes.DEVSTORAGE_READ_ONLY));

            Storage storage = new Storage.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    jsonFactory,
                    credential
            ).setApplicationName("shell-racing-legends").build();
            Storage.Objects.Get getRequest = storage.objects().get(BUCKET_NAME, OBJECT_NAME);
            StorageObject storageObject = getRequest.execute();

            System.out.println("Object Name: " + storageObject.getName());
            System.out.println("Size: " + storageObject.getSize());
            System.out.println("Updated: " + storageObject.getUpdated());
        return applicationResponse;
    }

    @Override
    public ApplicationResponse importPlayStoreDataByCSV(MultipartFile uploadedfile) throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            String file = uploadedfile.getOriginalFilename();
            String saveFileName = System.nanoTime() + "_" + file;
            String directoryPath = srlAnalyticsConfig.getFileUploadPath();

            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filepath = Paths.get(directoryPath, saveFileName).toString();
            File destFile = new File(filepath);

            uploadedfile.transferTo(destFile);

            String csvData = Files.readString(destFile.toPath());
            StringReader stringReader = new StringReader(csvData);
            CSVParser csvParser = CSVParser.parse(stringReader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            List<PlayStoreReportDTO> playStoreReportDTOS = new ArrayList<>();

            Map<String, String> headerToCountryMap = new HashMap<>();
            for (String header : csvParser.getHeaderNames()) {
                if (!header.equalsIgnoreCase("Date")) {
                    String country = header.substring(header.lastIndexOf(":") + 1).trim();
                    if(!country.equals("All countries / regions")) {
                        headerToCountryMap.put(header, country);
                    }
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
            for (CSVRecord record : csvParser) {
                String dateStr = record.get("Date").trim();
                Date date = sdf.parse(dateStr);
                Date formattedDate = sdf2.parse(sdf2.format(date));

                for (String header : headerToCountryMap.keySet()) {
                    String installStr = record.get(header).replace(",", "").trim(); // remove commas
                    if (!installStr.isEmpty()) {
                        long installs = Long.parseLong(installStr);
                        String country = headerToCountryMap.get(header);
                        Optional<Country> optionalCountry = countryRepository.findByCountryNameAndIsDeleted(country,0);
                        String countryCode =null;
                        if(optionalCountry.isPresent()) {
                           countryCode= optionalCountry.get().getCountry();
                        }
                        playStoreReportDTOS.add(new PlayStoreReportDTO(null, null, formattedDate, country, countryCode, installs));
                    }
                }
            }
            applicationResponse.setData(playStoreReportServiceImpl.saveAll(playStoreReportDTOS));
            applicationResponse.setData(playStoreReportDTOS);
            applicationResponse.setSuccess(true);

        }catch (Exception e){
            applicationResponse.setSuccess(false);
            applicationResponse.setError(e.getMessage());
        }
        return applicationResponse;
    }

    public static void extractReport(byte[] gzippedData, String outputPath) throws IOException {
        try (
                ByteArrayInputStream bais = new ByteArrayInputStream(gzippedData);
                GZIPInputStream gis = new GZIPInputStream(bais);
                FileOutputStream fos = new FileOutputStream(outputPath)
        ) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = gis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }
    public List<AppStoreSalesReportDTO> parseTsv(File tsvFile) throws IOException, ParseException {
        List<AppStoreSalesReportDTO> reportList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(tsvFile))) {
            String headerLine = reader.readLine(); // skip header
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t", -1); // split by tab, preserve empty fields

                AppStoreSalesReportDTO dto = new AppStoreSalesReportDTO();
                dto.setProvider(tokens[0]);
                dto.setProviderCountry(tokens[1]);
                dto.setSku(tokens[2]);
                dto.setDeveloper(tokens[3]);
                dto.setTitle(tokens[4]);
                dto.setVersion(tokens[5]);
                dto.setProductTypeIdentifier(tokens[6]);
                dto.setUnits(parseInt(tokens[7]));
                dto.setDeveloperProceeds(parseDouble(tokens[8]));
                dto.setBeginDate(simpleDateFormat.parse(tokens[9]));
                dto.setEndDate(simpleDateFormat.parse(tokens[10]));
                dto.setCustomerCurrency(tokens[11]);
                dto.setCountryCode(tokens[12]);
                dto.setCurrencyOfProceeds(tokens[13]);
                dto.setAppleIdentifier(tokens[14]);
                dto.setCustomerPrice(parseDouble(tokens[15]));
                dto.setPromoCode(tokens[16]);
                dto.setParentIdentifier(tokens[17]);
                dto.setSubscription(tokens[18]);
                dto.setPeriod(tokens[19]);
                dto.setCategory(tokens[20]);
                dto.setCmb(tokens[21]);
                dto.setDevice(tokens[22]);
                dto.setSupportedPlatforms(tokens[23]);
                dto.setProceedsReason(tokens[24]);
                dto.setPreservedPricing(tokens[25]);
                dto.setClient(tokens[26]);
                dto.setOrderType(tokens[27]);
                reportList.add(dto);
            }
        }
        appStoreSalesServiceImpl.saveAll(reportList);
        return reportList;
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }


    @Override
    public ApplicationResponse exportMultiTabExcel(HttpServletResponse response, String startDate, String endDate, String country) throws Exception {

        try (Workbook workbook = new XSSFWorkbook()) {

            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            Map<String, String> formData = new HashMap<>();
            formData.put("startDate", startDate);
            formData.put("endDate", endDate);

            if (country != null && !country.trim().isEmpty()) {
                formData.put("country", country.trim());
            } else {
                formData.put("country", "");
            }



            List<Map<String, Object>> appLaunchData = extractData(appLaunchedService.getCountryWiseData(formData));

            Map<String, String> countryMap = new HashMap<>();
            for (Map<String, Object> row : appLaunchData) {
                Object codeObj = row.get("country");
                Object nameObj = row.get("countryName");
                if (codeObj != null && nameObj != null) {
                    countryMap.put(codeObj.toString(), nameObj.toString());
                }
            }


            addSheet(workbook, "App Launches", extractData(appLaunchedService.getCountryWiseData(formData)), boldStyle, Collections.emptyMap());
            addSheet(workbook, "Brand Views", extractData(brandViewedGarageService.getCountryWiseData(formData)), boldStyle, Collections.emptyMap());
            addSheet(workbook, "Cars Unlocked", extractData(carUnlockedService.getCarUnlockedData(formData)), boldStyle, countryMap);
            addSheet(workbook, "Remote Control Played", extractData(rcPlayedService.getCountryWiseData(formData)), boldStyle, countryMap);
            addSheet(workbook, "Race Played", extractData(racePlayedService.getCountryWiseData(formData)), boldStyle, countryMap);
            addSheet(workbook, "Cars Average", extractData(carUnlockedService.getCountryWiseAverages(formData)), boldStyle, Collections.emptyMap());

            String fileName = "shell_events_" + System.nanoTime() + ".xlsx";
            log.info("Sending Excel file: {}", fileName);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            workbook.write(response.getOutputStream());
            response.flushBuffer();

            log.info("Excel generated successfully");

        } catch (Exception e) {
            log.error("Error generating Excel: {}", e.getMessage(), e);
            throw new Exception("Failed to generate Excel: " + e.getMessage(), e);
        }
        return ApplicationResponse.builder().build();
    }

    private List<Map<String, Object>> extractData(ApplicationResponse response) {
        if (response == null || response.getData() == null) {
            return Collections.emptyList();
        }

        Object dataObj = response.getData();

        if (dataObj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) dataObj;

            Optional<Object> firstList = map.values().stream()
                    .filter(v -> v instanceof List)
                    .findFirst();

            if (firstList.isPresent()) {
                dataObj = firstList.get();
            } else {
                return Collections.emptyList();
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();

        if (dataObj instanceof List<?>) {
            for (Object obj : (List<?>) dataObj) {
                Map<String, Object> map = new LinkedHashMap<>();
                if (obj instanceof Map) {
                    map.putAll((Map<String, Object>) obj);
                } else {
                    for (Field field : obj.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        try {
                            map.put(field.getName(), field.get(obj));
                        } catch (IllegalAccessException ignored) {}
                    }
                }
                result.add(map);
            }
        }

        return result;
    }


    private static final Map<String, Set<String>> EXCLUDED_COLUMNS = Map.of(
            "App Launches", Set.of("country"),
            "Brand Views", Set.of("country"),
            "Cars Average", Set.of("totalCarsUnlocked", "totalRCPlayed", "totalRacePlayed", "countryId", "countryCode")
    );

    private static final Map<String, String> COLUMN_NAME_MAPPING = Map.ofEntries(
            Map.entry("countryName", "Country Name"),
            Map.entry("carName", "Car Name"),
            Map.entry("totalRcDuration", "Total RC Duration"),
            Map.entry("cumulativeRcDuration", "Cumulative RC Duration"),
            Map.entry("totalUsers", "Customers"),
            Map.entry("avgCars", "Average Cars"),
            Map.entry("avgRC", "Average Remote Control Played (Minutes)"),
            Map.entry("avgRace", "Average Race Played (Minutes)"),
            Map.entry("views", "Views"),
            Map.entry("unlocked", "Unlocked"),
            Map.entry("cumulative", "Cumulative"),
            Map.entry("launches","Launches")
    );

    private static final Set<String> CONTAINER_SHEETS = Set.of(
            "Cars Unlocked",
            "Remote Control Played",
            "Race Played"
    );



    private void addSheet(Workbook workbook, String sheetName, List<Map<String, Object>> data, CellStyle boldStyle, Map<String, String> countryMap){
        if (data == null || data.isEmpty()) {
            log.warn("No data found for {}", sheetName);
            return;
        }

        Set<String> excludedColumns = EXCLUDED_COLUMNS.getOrDefault(sheetName, Collections.emptySet());
        Sheet sheet = workbook.createSheet(sheetName);
        int rowIndex = 0;

        boolean moveCountryNameFirst = sheetName.equals("App Launches")
                || sheetName.equals("Brand Views")
                || sheetName.equals("Cars Average");

        if (CONTAINER_SHEETS.contains(sheetName)) {
            Map<String, List<Map<String, Object>>> groupedData = new TreeMap<>();
            for (Map<String, Object> row : data) {
                Object countryObj = row.get("country");
                String countryKey = (countryObj != null) ? countryObj.toString() : "Unknown";
                groupedData.computeIfAbsent(countryKey, k -> new ArrayList<>()).add(row);
            }

            for (Map.Entry<String, List<Map<String, Object>>> entry : groupedData.entrySet()) {
                String country = entry.getKey();
                List<Map<String, Object>> countryData = entry.getValue();

                Row countryHeader = sheet.createRow(rowIndex++);
                String countryFullName = countryMap.getOrDefault(country, country);
                Cell countryCell = countryHeader.createCell(0);
                countryCell.setCellValue(countryFullName + " (" + country + ")");
                countryCell.setCellStyle(boldStyle);



                Map<String, Object> firstRow = countryData.get(0);
                List<String> keys = new ArrayList<>(firstRow.keySet());
                keys.remove("country");
                keys.removeAll(excludedColumns);

                Row headerRow = sheet.createRow(rowIndex++);
                for (int i = 0; i < keys.size(); i++) {
                    if ("totalRacDurationInHours".equals(keys.get(i)) ||"cumulativeRacDurationInHours".equals(keys.get(i)) ) {
                        continue;
                    }
                    Cell cell = headerRow.createCell(i);
                    String headerName = COLUMN_NAME_MAPPING.getOrDefault(keys.get(i), keys.get(i));
                    cell.setCellValue(headerName);
                    cell.setCellStyle(boldStyle);
                }


                for (Map<String, Object> rowData : countryData) {
                    Row row = sheet.createRow(rowIndex++);
                    for (int i = 0; i < keys.size(); i++) {
                        Object value = rowData.get(keys.get(i));
                        if ("totalRacDurationInHours".equals(keys.get(i)) ||"cumulativeRacDurationInHours".equals(keys.get(i)) ) {
                            continue;
                        }
                        if (i == 0) {
                            row.createCell(i).setCellValue(value != null ? value.toString() : "");
                        } else {
                            String formattedValue = "";
                            try {
                                if (value != null) {
                                    double numericValue = Double.parseDouble(value.toString());
                                    if ("totalRcDuration".equals(keys.get(i))){
                                        formattedValue = (String) rowData.get(keys.get(i+2));
                                    }
                                    if("cumulativeRcDuration".equals(keys.get(i)) ) {
                                        formattedValue = (String) rowData.get(keys.get(i+2));
                                    }
//                                    if (sheetName.equals("Remote Control Played") || sheetName.equals("Race Played")) {
//                                        double totalMinutes = numericValue ;
//                                        long hrs = (long) Math.floor(totalMinutes / 60);
//                                        long mins = Math.round(totalMinutes % 60);
//
//                                        if (hrs > 0 && mins > 0)
//                                            formattedValue = hrs + " hr " + mins + " min";
//                                        else if (hrs > 0)
//                                            formattedValue = hrs + " hr";
//                                        else
//                                            formattedValue = mins + " min";
//                                    } else {
//                                        formattedValue = String.format("%.0f", numericValue);
//                                    }
                                }
                            } catch (NumberFormatException e) {
                                formattedValue = "-";
                            }

                            row.createCell(i).setCellValue(formattedValue);
                        }
                    }
                }



                sheet.createRow(rowIndex++);
            }

        }
        else {
            Map<String, Object> firstRow = data.get(0);
            List<String> keys = new ArrayList<>(firstRow.keySet());

            if (sheetName.equals("Cars Average")) {
                for (Map<String, Object> row : data) {
                    Object nameObj = row.get("countryName");
                    Object codeObj = row.get("countryCode");
                    if (nameObj != null && codeObj != null) {
                        row.put("countryName", nameObj + " (" + codeObj + ")");
                    }
                }
            }

            if (moveCountryNameFirst && keys.contains("countryName")) {
                keys.remove("countryName");
                keys.add(0, "countryName");
            }


            Row headerRow = sheet.createRow(rowIndex++);
            int headerIndex = 0;
            for (String key : keys) {
                if (excludedColumns.contains(key)) continue;
                Cell cell = headerRow.createCell(headerIndex++);
                String headerName = COLUMN_NAME_MAPPING.getOrDefault(key, key);
                cell.setCellValue(headerName);
                cell.setCellStyle(boldStyle);
            }


            for (Map<String, Object> rowData : data) {
                Row row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                for (String key : keys) {
                    if (excludedColumns.contains(key)) continue;
                    Object value = rowData.get(key);

                    if (sheetName.equals("Cars Average") && value != null) {
                        if (key.equals("avgRC") || key.equals("avgRace")) {
                            try {
                                double minutes = Double.parseDouble(value.toString()) / 60.0;
                                value = String.format("%.2f", minutes);
                            } catch (NumberFormatException ignored) {}
                        }
                    }

                    if (sheetName.equals("Cars Average") && value != null) {
                        if (key.equals("avgCars")) {
                            try {
                                double num = Double.parseDouble(value.toString());
                                double rounded = Math.round(num * 100.0) / 100.0;
                                value = String.format("%.2f", rounded);
                            } catch (NumberFormatException ignored) {}
                        }
                    }


                    row.createCell(cellIndex++).setCellValue(value != null ? value.toString() : "");
                }
            }

        }

        Map<String, Object> firstRow = data.get(0);
        int columnCount = (int) firstRow.keySet().stream()
                .filter(k -> !excludedColumns.contains(k) && (!CONTAINER_SHEETS.contains(sheetName) || !k.equals("country")))
                .count();
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}



