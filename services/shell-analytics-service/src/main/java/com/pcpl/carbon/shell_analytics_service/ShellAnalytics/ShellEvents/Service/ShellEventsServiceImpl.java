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
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public ApplicationResponse exportMultiTabExcel(HttpServletResponse response, String startDate, String endDate, String country, String reportType) throws Exception {

        boolean weekly = "weekly".equalsIgnoreCase(reportType);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            Map<String, String> formData = new HashMap<>();
            formData.put("startDate", startDate);
            formData.put("endDate", endDate);
            formData.put("country", (country != null && !country.trim().isEmpty()) ? country.trim() : "");

            // Full-range data (same services the dashboard uses) – provides the all-time Cumulative
            // column and the master list of countries. Used by both report types.
            DataBundle full = gather(formData);

            // Ordered master list of countries (code -> display name), driven by the averages dataset
            // and back-filled from any other dataset that references a country the averages missed.
            LinkedHashMap<String, String> countries = new LinkedHashMap<>();
            for (Map<String, Object> a : full.averages) {
                String code = str(a.get("countryCode"));
                String name = str(a.get("countryName"));
                if (code != null && !code.isEmpty()) {
                    countries.putIfAbsent(code, (name != null && !name.isEmpty()) ? name : code);
                }
            }
            collectCountries(countries, full.cars);
            collectCountries(countries, full.rc);
            collectCountries(countries, full.race);
            for (Map.Entry<String, List<Map<String, Object>>> e : full.appStore.entrySet()) {
                countries.putIfAbsent(e.getKey(), storeCountryName(e.getValue(), e.getKey()));
            }
            for (Map.Entry<String, List<Map<String, Object>>> e : full.playStore.entrySet()) {
                countries.putIfAbsent(e.getKey(), storeCountryName(e.getValue(), e.getKey()));
            }

            // For the Weekly report only: one data bundle per week in the range (each scoped to that
            // week's dates, so the weekly columns are calculated exactly like the dashboard's range totals).
            List<Week> weeks = weekly ? computeWeeks(startDate, endDate) : Collections.emptyList();
            List<DataBundle> weeklyData = new ArrayList<>();
            if (weekly) {
                for (Week w : weeks) {
                    Map<String, String> wf = new HashMap<>();
                    wf.put("startDate", w.start());
                    wf.put("endDate", w.end());
                    wf.put("country", formData.get("country"));
                    // Per-week bundles never render Store Performance, so skip the App/Play Store
                    // queries here (2 fewer, heavier queries per week).
                    weeklyData.add(gather(wf, false));
                }
            }

            Map<String, XSSFCellStyle> styles = buildStyles(workbook);
            String period = formatPeriod(startDate) + " TILL " + formatPeriod(endDate);

            if (countries.isEmpty() || (weekly && weeks.isEmpty())) {
                // No data for any country: still return a valid, openable workbook.
                XSSFSheet sheet = workbook.createSheet("DASHBOARD");
                sheet.setDisplayGridlines(false);
                fillRow(sheet, 0, 0, 15, styles.get("title"));
                put(sheet, 0, 0, "REPORT - " + period, styles.get("title"));
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));
            } else {
                Set<String> usedNames = new HashSet<>();
                for (Map.Entry<String, String> c : countries.entrySet()) {
                    String sheetName = uniqueSheetName(usedNames, c.getValue());
                    if (weekly) {
                        buildWeeklySheet(workbook, styles, sheetName, period, c.getKey(), c.getValue(), weeks, weeklyData, full);
                    } else {
                        buildStandardSheet(workbook, styles, sheetName, period, c.getKey(), c.getValue(), full);
                    }
                }
            }

            String fileName = "shell_events_" + System.nanoTime() + ".xlsx";
            log.info("Generating Excel file: {}", fileName);

            // Build the workbook fully in memory, then write it to the response in one bulk copy with
            // a known Content-Length. Writing POI's package straight to the servlet output stream
            // streams it to the browser over a potentially slow connection; if the client aborts the
            // slow download, the package save fails part-way (e.g. "/docProps/app.xml failed to be
            // saved in the stream") and leaves a half-committed response that can no longer be reset.
            // Buffering first makes the save reliable and the socket write a single fast copy.
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            workbook.write(buffer);
            byte[] bytes = buffer.toByteArray();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.flushBuffer();

            log.info("Excel generated successfully ({} bytes)", bytes.length);

        } catch (Exception e) {
            log.error("Error generating Excel: {}", e.getMessage(), e);
            throw new Exception("Failed to generate Excel: " + e.getMessage(), e);
        }
        return ApplicationResponse.builder().build();
    }

    // ---------------------------------------------------------------------------------------------
    //  Multi-tab Excel export – one "DASHBOARD-<COUNTRY>" sheet per country, laid out and styled to
    //  match the SRL analytics reference template (App Launches / Brand Views / Users + Period KPIs,
    //  Cars Unlocked, Remote Control Played, Race Played, Store Performance), populated dynamically.
    // ---------------------------------------------------------------------------------------------

    private static final int CAT_COL = 1;                // car-name column (B) – chart categories
    private static final int CHART_ROWS = 15;            // rows a chart spans; each section advances past this so charts never overlap

    private void buildWeeklySheet(XSSFWorkbook wb, Map<String, XSSFCellStyle> s, String sheetName, String period,
                                   String code, String name, List<Week> weeks,
                                   List<DataBundle> weekly, DataBundle full) {

        int nw = weeks.size();
        // Column geometry: three side-by-side tables + a Period box. Each table is
        // 1 label column + nw weekly columns + 1 Cumulative column, with a one-column gap between.
        int tw = nw + 2;
        int appC0 = 1;
        int brandC0 = appC0 + tw + 1;
        int usersC0 = brandC0 + tw + 1;
        int periodC0 = usersC0 + tw + 1;
        int lastCol = periodC0 + nw + 1; // right edge of the Period box = banner width

        XSSFSheet sheet = wb.createSheet(sheetName);
        sheet.setDisplayGridlines(false); // clean, template-style look (no default Excel gridlines)
        applyColumnWidths(sheet, lastCol, periodC0);
        XSSFDrawing drawing = sheet.createDrawingPatriarch();

        // Row 0 – report title banner (text lives in the merge anchor, column A).
        fillRow(sheet, 0, 0, lastCol, s.get("title"));
        put(sheet, 0, 0, "REPORT - " + period, s.get("title"));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastCol));
        sheet.getRow(0).setHeightInPoints(34);
        fillRow(sheet, 1, 0, lastCol, s.get("bandBig"));

        // Rows 3-6 – App Launches / Brand Views / Users tables + the "Period" KPI box.
        labelCell(sheet, 3, appC0, appC0 + tw - 1, "APP LAUNCHES", s.get("yellowLabel"));
        labelCell(sheet, 3, brandC0, brandC0 + tw - 1, "BRAND VIEWS", s.get("yellowLabel"));
        labelCell(sheet, 3, usersC0, usersC0 + tw - 1, "USERS", s.get("yellowLabel"));
        put(sheet, 3, periodC0, "Period", s.get("periodHdr"));
        for (int i = 0; i < nw; i++) put(sheet, 3, periodC0 + 1 + i, weeks.get(i).label(), s.get("weekHdr"));
        put(sheet, 3, periodC0 + 1 + nw, "Cumulative", s.get("hdrRight"));

        summaryHeader(sheet, 4, appC0, weeks, s);
        summaryHeader(sheet, 4, brandC0, weeks, s);
        summaryHeader(sheet, 4, usersC0, weeks, s);

        // Per-week values for the three top tables + the Cars/RC/Race grand totals (used by the KPI box).
        double[] appWk = new double[nw], brandWk = new double[nw], usersWk = new double[nw];
        double[] carsGT = new double[nw], rcGT = new double[nw], raceGT = new double[nw];
        for (int i = 0; i < nw; i++) {
            DataBundle b = weekly.get(i);
            appWk[i]   = toDouble(findRow(b.appLaunch, code, name).get("launches"));
            brandWk[i] = toDouble(findRow(b.brand, code, name).get("views"));
            usersWk[i] = toDouble(findRow(b.averages, code, name).get("totalUsers"));
            carsGT[i]  = sumField(filterByCountry(b.cars, code, name), "unlocked");
            rcGT[i]    = roundSumField(filterByCountry(b.rc, code, name), "totalRcDuration");
            raceGT[i]  = roundSumField(filterByCountry(b.race, code, name), "totalRcDuration");
        }
        double appCum   = toDouble(findRow(full.appLaunch, code, name).get("cumulative"));
        double brandCum = toDouble(findRow(full.brand, code, name).get("cumulative"));
        double usersCum = toDouble(findRow(full.averages, code, name).get("cummulativeUsers"));

        put(sheet, 5, appC0, name, s.get("ctyName"));
        weekRow(sheet, 5, appC0, appWk, appCum, s.get("num"));
        put(sheet, 5, brandC0, name, s.get("ctyName"));
        weekRow(sheet, 5, brandC0, brandWk, brandCum, s.get("num"));
        put(sheet, 5, usersC0, name, s.get("ctyName"));
        weekRow(sheet, 5, usersC0, usersWk, usersCum, s.get("num"));

        put(sheet, 6, appC0, "Grand Total", s.get("gtLabel"));
        weekRow(sheet, 6, appC0, appWk, appCum, s.get("gtNum"));
        put(sheet, 6, brandC0, "Grand Total", s.get("gtLabel"));
        weekRow(sheet, 6, brandC0, brandWk, brandCum, s.get("gtNum"));
        put(sheet, 6, usersC0, "Grand Total", s.get("gtLabel"));
        weekRow(sheet, 6, usersC0, usersWk, usersCum, s.get("gtNum"));

        // Period KPI box – one value per week + Cumulative.
        double carsCum = sumField(filterByCountry(full.cars, code, name), "cumulative");
        double rcCum   = roundSumField(filterByCountry(full.rc, code, name), "cumulativeRcDuration");
        double raceCum = roundSumField(filterByCountry(full.race, code, name), "cumulativeRcDuration");
        put(sheet, 4, periodC0, "Average Cars Per Users", s.get("periodHdr"));
        put(sheet, 5, periodC0, "Average Remote Control Played", s.get("periodHdr"));
        put(sheet, 6, periodC0, "Average Race Played", s.get("periodHdr"));
        for (int i = 0; i < nw; i++) {
            int c = periodC0 + 1 + i;
            put(sheet, 4, c, usersWk[i] > 0 ? fmt2(carsGT[i] / usersWk[i]) : "", s.get("kpiVal"));
            put(sheet, 5, c, usersWk[i] > 0 ? formatDuration(rcGT[i] / usersWk[i]) : "", s.get("kpiVal"));
            put(sheet, 6, c, usersWk[i] > 0 ? formatDuration(raceGT[i] / usersWk[i]) : "", s.get("kpiVal"));
        }
        int pc = periodC0 + 1 + nw;
        put(sheet, 4, pc, usersCum > 0 ? fmt2(carsCum / usersCum) : "", s.get("kpiVal"));
        put(sheet, 5, pc, usersCum > 0 ? formatDuration(rcCum / usersCum) : "", s.get("kpiVal"));
        put(sheet, 6, pc, usersCum > 0 ? formatDuration(raceCum / usersCum) : "", s.get("kpiVal"));

        int r = 9; // Excel row 10

        // Cars Unlocked ------------------------------------------------------------------------
        r = sectionBanner(sheet, r, "CARS UNLOCKED", name, s, lastCol);
        int chartRow = r + 1;
        r++; // spacer
        carsHeader(sheet, r++, weeks, s);
        List<String> carNames = carOrder(full.cars, code, name);
        int ds = r;
        double[] carsColTotal = new double[nw];
        double carsColCum = 0;
        for (String carName : carNames) {
            double[] wk = new double[nw];
            for (int i = 0; i < nw; i++) {
                wk[i] = carField(weekly.get(i).cars, code, name, carName, "unlocked");
                carsColTotal[i] += wk[i];
            }
            double cum = carField(full.cars, code, name, carName, "cumulative");
            carsColCum += cum;
            put(sheet, r, 1, carName, s.get("carName"));
            weekRow(sheet, r, 1, wk, cum, s.get("num"));
            r++;
        }
        int de = r - 1;
        put(sheet, r, 1, "Grand Total", s.get("gtLabel"));
        weekRow(sheet, r, 1, carsColTotal, carsColCum, s.get("gtAccentNum"));
        r++;
        addBarChart(drawing, sheet, chartRow, ds, de, nw, lastCol, null);
        r = Math.max(r, chartRow + CHART_ROWS) + 2;

        // Remote Control Played ----------------------------------------------------------------
        r = playedSection(sheet, drawing, s, r, "REMOTE CONTROL PLAYED", name, code, weeks, weekly, full, lastCol, false);
        // Race Played --------------------------------------------------------------------------
        playedSection(sheet, drawing, s, r, "RACE PLAYED", name, code, weeks, weekly, full, lastCol, true);

        // Store Performance section intentionally omitted (removed per request).
    }

    // ===== Standard report (first template): single "Last week" + "Cumulative" columns ==========

    private void buildStandardSheet(XSSFWorkbook wb, Map<String, XSSFCellStyle> s, String sheetName, String period,
                                    String code, String name, DataBundle full) {
        int lastCol = 15;
        XSSFSheet sheet = wb.createSheet(sheetName);
        sheet.setDisplayGridlines(false);
        applyColumnWidthsStd(sheet);
        XSSFDrawing drawing = sheet.createDrawingPatriarch();

        fillRow(sheet, 0, 0, lastCol, s.get("title"));
        put(sheet, 0, 0, "REPORT - " + period, s.get("title"));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastCol));
        sheet.getRow(0).setHeightInPoints(34);
        fillRow(sheet, 1, 0, lastCol, s.get("bandBig"));

        Map<String, Object> app = findRow(full.appLaunch, code, name);
        Map<String, Object> brand = findRow(full.brand, code, name);
        Map<String, Object> avg = findRow(full.averages, code, name);

        labelCell(sheet, 3, 1, 3, "APP LAUNCHES", s.get("yellowLabel"));
        labelCell(sheet, 3, 5, 7, "BRAND VIEWS", s.get("yellowLabel"));
        labelCell(sheet, 3, 9, 11, "USERS", s.get("yellowLabel"));
        put(sheet, 3, 13, null, s.get("periodHdr"));
        labelCell(sheet, 3, 14, 15, "Period", s.get("periodHdr"));

        summaryHeaderStd(sheet, 4, 1, s);
        summaryHeaderStd(sheet, 4, 5, s);
        summaryHeaderStd(sheet, 4, 9, s);
        put(sheet, 4, 13, "Average Cars Per Users", s.get("periodHdr"));
        labelCell(sheet, 4, 14, 15, fmt2(toDouble(avg.get("avgCars"))), s.get("kpiVal"));

        put(sheet, 5, 1, name, s.get("ctyName"));
        put(sheet, 5, 2, toDouble(app.get("launches")), s.get("num"));
        put(sheet, 5, 3, toDouble(app.get("cumulative")), s.get("num"));
        put(sheet, 5, 5, name, s.get("ctyName"));
        put(sheet, 5, 6, toDouble(brand.get("views")), s.get("num"));
        put(sheet, 5, 7, toDouble(brand.get("cumulative")), s.get("num"));
        put(sheet, 5, 9, name, s.get("ctyName"));
        put(sheet, 5, 10, toDouble(avg.get("totalUsers")), s.get("num"));
        put(sheet, 5, 11, toDouble(avg.get("cummulativeUsers")), s.get("num"));
        put(sheet, 5, 13, "Average Remote Control Played", s.get("periodHdr"));
        labelCell(sheet, 5, 14, 15, formatDuration(toDouble(avg.get("avgRC"))), s.get("kpiVal"));

        grandTotalStd(sheet, 6, 1, toDouble(app.get("launches")), toDouble(app.get("cumulative")), s);
        grandTotalStd(sheet, 6, 5, toDouble(brand.get("views")), toDouble(brand.get("cumulative")), s);
        grandTotalStd(sheet, 6, 9, toDouble(avg.get("totalUsers")), toDouble(avg.get("cummulativeUsers")), s);
        put(sheet, 6, 13, "Average Race Played", s.get("periodHdr"));
        labelCell(sheet, 6, 14, 15, formatDuration(toDouble(avg.get("avgRace"))), s.get("kpiVal"));

        int r = 9;

        // Cars Unlocked ------------------------------------------------------------------------
        List<Map<String, Object>> cars = filterByCountry(full.cars, code, name);
        r = sectionBanner(sheet, r, "CARS UNLOCKED", name, s, lastCol);
        int chartRow = r + 1;
        r++; // spacer
        carsHeaderStd(sheet, r++, s);
        int ds = r;
        double lwSum = 0, cumSum = 0;
        for (Map<String, Object> car : cars) {
            double lw = toDouble(car.get("unlocked")), cu = toDouble(car.get("cumulative"));
            put(sheet, r, 1, str(car.get("carName")), s.get("carName"));
            put(sheet, r, 2, lw, s.get("num"));
            put(sheet, r, 3, cu, s.get("num"));
            lwSum += lw; cumSum += cu; r++;
        }
        int de = r - 1;
        put(sheet, r, 1, "Grand Total", s.get("gtLabel"));
        put(sheet, r, 2, lwSum, s.get("gtAccentNum"));
        put(sheet, r, 3, cumSum, s.get("gtAccentNum"));
        r++;
        addBarChartStd(drawing, sheet, chartRow, ds, de, null);
        r = Math.max(r, chartRow + CHART_ROWS) + 2;

        r = playedSectionStd(sheet, drawing, s, r, "REMOTE CONTROL PLAYED", name, filterByCountry(full.rc, code, name), lastCol);
        playedSectionStd(sheet, drawing, s, r, "RACE PLAYED", name, filterByCountry(full.race, code, name), lastCol);

        // Store Performance section intentionally omitted (removed per request).
    }

    private int playedSectionStd(XSSFSheet sheet, XSSFDrawing drawing, Map<String, XSSFCellStyle> s, int r,
                                 String title, String name, List<Map<String, Object>> cars, int lastCol) {
        r = sectionBanner(sheet, r, title, name, s, lastCol);
        int chartRow = r + 1;
        r++; // spacer
        labelCell(sheet, r, 2, 3, "Played Minutes", s.get("sub"));
        r++;
        carsHeaderStd(sheet, r++, s);
        int ds = r;
        double lwMin = 0, cumMin = 0;
        for (Map<String, Object> car : cars) {
            double lw = Math.round(toDouble(car.get("totalRcDuration")));
            double cu = Math.round(toDouble(car.get("cumulativeRcDuration")));
            put(sheet, r, 1, str(car.get("carName")), s.get("carName"));
            put(sheet, r, 2, lw, s.get("num"));
            put(sheet, r, 3, cu, s.get("num"));
            lwMin += lw; cumMin += cu; r++;
        }
        int de = r - 1;
        put(sheet, r, 1, "Grand Total", s.get("gtLabel"));
        put(sheet, r, 2, lwMin, s.get("gtNum"));
        put(sheet, r, 3, cumMin, s.get("gtNum"));
        r++;
        addBarChartStd(drawing, sheet, chartRow, ds, de, "Played Duration");
        r++; // spacer
        labelCell(sheet, r, 2, 3, "Played Hours", s.get("sub"));
        r++;
        carsHeaderStd(sheet, r++, s);
        for (Map<String, Object> car : cars) {
            put(sheet, r, 1, str(car.get("carName")), s.get("carName"));
            put(sheet, r, 2, str(car.get("totalRacDurationInHours")), s.get("str"));
            put(sheet, r, 3, str(car.get("cumulativeRacDurationInHours")), s.get("str"));
            r++;
        }
        put(sheet, r, 1, "Grand Total", s.get("gtLabel"));
        put(sheet, r, 2, formatDuration(lwMin), s.get("gtAccentStr"));
        put(sheet, r, 3, formatDuration(cumMin), s.get("gtAccentStr"));
        r++;
        r = Math.max(r, chartRow + CHART_ROWS) + 2;
        return r;
    }

    private void summaryHeaderStd(XSSFSheet sheet, int r, int c0, Map<String, XSSFCellStyle> s) {
        put(sheet, r, c0, "COUNTRY", s.get("hdrLeft"));
        put(sheet, r, c0 + 1, "Period", s.get("hdrRight"));
        put(sheet, r, c0 + 2, "Cumulative", s.get("hdrRight"));
    }

    private void carsHeaderStd(XSSFSheet sheet, int r, Map<String, XSSFCellStyle> s) {
        put(sheet, r, 1, "CARS", s.get("carsHdr"));
        put(sheet, r, 2, "Period", s.get("hdrRight"));
        put(sheet, r, 3, "Cumulative", s.get("hdrRight"));
    }

    private void grandTotalStd(XSSFSheet sheet, int r, int c0, double lw, double cum, Map<String, XSSFCellStyle> s) {
        put(sheet, r, c0, "Grand Total", s.get("gtLabel"));
        put(sheet, r, c0 + 1, lw, s.get("gtNum"));
        put(sheet, r, c0 + 2, cum, s.get("gtNum"));
    }

    private void addBarChartStd(XSSFDrawing drawing, XSSFSheet sheet, int topRow, int dataStart, int dataEnd, String title) {
        if (dataEnd < dataStart) return;
        try {
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, topRow, 14, topRow + CHART_ROWS);
            XSSFChart chart = drawing.createChart(anchor);
            if (title != null) { chart.setTitleText(title); chart.setTitleOverlay(false); }
            chart.getOrAddLegend().setPosition(LegendPosition.BOTTOM);
            XDDFCategoryAxis bottom = chart.createCategoryAxis(AxisPosition.BOTTOM);
            XDDFValueAxis left = chart.createValueAxis(AxisPosition.LEFT);
            XDDFDataSource<String> cats = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                    new CellRangeAddress(dataStart, dataEnd, CAT_COL, CAT_COL));
            XDDFNumericalDataSource<Double> lw = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                    new CellRangeAddress(dataStart, dataEnd, 2, 2));
            XDDFNumericalDataSource<Double> cum = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                    new CellRangeAddress(dataStart, dataEnd, 3, 3));
            XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottom, left);
            data.setBarDirection(BarDirection.COL);
            XDDFChartData.Series s1 = data.addSeries(cats, lw);
            s1.setTitle("Period", null);
            setSeriesColor(s1, 0x15, 0x60, 0x82);
            XDDFChartData.Series s2 = data.addSeries(cats, cum);
            s2.setTitle("Cumulative", null);
            setSeriesColor(s2, 0xE9, 0x71, 0x32);
            chart.plot(data);
        } catch (Exception e) {
            log.warn("Skipping chart ({}): {}", title, e.getMessage());
        }
    }

    private void applyColumnWidthsStd(XSSFSheet sheet) {
        double[] widths = {0, 39.4, 27.9, 19.1, 11.1, 19.1, 14.4, 13.3, 11.1, 19.1, 17.3, 15.2, 33.1, 21.5, 14.4, 12.0};
        for (int c = 1; c < widths.length; c++) sheet.setColumnWidth(c, (int) (widths[c] * 256));
    }

    // ===== Weekly report (second template): one column per week + Cumulative ====================

    /** Renders a Remote-Control / Race "Played Minutes" + "Played Hours" block and returns the next row. */
    private int playedSection(XSSFSheet sheet, XSSFDrawing drawing, Map<String, XSSFCellStyle> s, int r,
                              String title, String name, String code, List<Week> weeks,
                              List<DataBundle> weekly, DataBundle full, int lastCol, boolean race) {
        int nw = weeks.size();
        r = sectionBanner(sheet, r, title, name, s, lastCol);
        int chartRow = r + 1;
        r++; // spacer

        List<String> carNames = carOrder(race ? full.race : full.rc, code, name);

        // Played Minutes
        labelCell(sheet, r, 2, 3, "Played Minutes", s.get("sub"));
        r++;
        carsHeader(sheet, r++, weeks, s);
        int ds = r;
        double[] colTot = new double[nw];
        double colCum = 0;
        for (String carName : carNames) {
            double[] wk = new double[nw];
            for (int i = 0; i < nw; i++) {
                wk[i] = Math.round(carField(race ? weekly.get(i).race : weekly.get(i).rc, code, name, carName, "totalRcDuration"));
                colTot[i] += wk[i];
            }
            double cum = Math.round(carField(race ? full.race : full.rc, code, name, carName, "cumulativeRcDuration"));
            colCum += cum;
            put(sheet, r, 1, carName, s.get("carName"));
            weekRow(sheet, r, 1, wk, cum, s.get("num"));
            r++;
        }
        int de = r - 1;
        put(sheet, r, 1, "Grand Total", s.get("gtLabel"));
        weekRow(sheet, r, 1, colTot, colCum, s.get("gtNum"));
        r++;
        addBarChart(drawing, sheet, chartRow, ds, de, nw, lastCol, "Played Duration");
        r++; // spacer

        // Played Hours
        labelCell(sheet, r, 2, 3, "Played Hours", s.get("sub"));
        r++;
        carsHeader(sheet, r++, weeks, s);
        for (String carName : carNames) {
            put(sheet, r, 1, carName, s.get("carName"));
            for (int i = 0; i < nw; i++) {
                put(sheet, r, 2 + i, carFieldStr(race ? weekly.get(i).race : weekly.get(i).rc, code, name, carName, "totalRacDurationInHours"), s.get("str"));
            }
            put(sheet, r, 2 + nw, carFieldStr(race ? full.race : full.rc, code, name, carName, "cumulativeRacDurationInHours"), s.get("str"));
            r++;
        }
        put(sheet, r, 1, "Grand Total", s.get("gtLabel"));
        for (int i = 0; i < nw; i++) put(sheet, r, 2 + i, formatDuration(colTot[i]), s.get("gtAccentStr"));
        put(sheet, r, 2 + nw, formatDuration(colCum), s.get("gtAccentStr"));
        r++;
        // Ensure the next section starts below both this section's tables and its chart.
        r = Math.max(r, chartRow + CHART_ROWS) + 2;
        return r;
    }

    // ----- small layout helpers -----

    private int sectionBanner(XSSFSheet sheet, int r, String title, String countryName, Map<String, XSSFCellStyle> s, int lastCol) {
        fillRow(sheet, r, 0, lastCol, s.get("bandBig"));
        put(sheet, r++, 1, title, s.get("bandBig"));
        if (countryName != null) {
            fillRow(sheet, r, 0, lastCol, s.get("band"));
            put(sheet, r++, 1, countryName, s.get("band"));
        }
        return r;
    }

    private void summaryHeader(XSSFSheet sheet, int r, int c0, List<Week> weeks, Map<String, XSSFCellStyle> s) {
        put(sheet, r, c0, "COUNTRY", s.get("hdrLeft"));
        for (int i = 0; i < weeks.size(); i++) put(sheet, r, c0 + 1 + i, weeks.get(i).label(), s.get("weekHdr"));
        put(sheet, r, c0 + 1 + weeks.size(), "Cumulative", s.get("hdrRight"));
    }

    private void carsHeader(XSSFSheet sheet, int r, List<Week> weeks, Map<String, XSSFCellStyle> s) {
        put(sheet, r, 1, "CARS", s.get("carsHdr"));
        for (int i = 0; i < weeks.size(); i++) put(sheet, r, 2 + i, weeks.get(i).label(), s.get("weekHdr"));
        put(sheet, r, 2 + weeks.size(), "Cumulative", s.get("hdrRight"));
    }

    /** Writes nw weekly values starting at c0+1, then the Cumulative value at c0+1+nw. */
    private void weekRow(XSSFSheet sheet, int r, int c0, double[] weekVals, double cum, XSSFCellStyle style) {
        for (int i = 0; i < weekVals.length; i++) put(sheet, r, c0 + 1 + i, weekVals[i], style);
        put(sheet, r, c0 + 1 + weekVals.length, cum, style);
    }

    private void labelCell(XSSFSheet sheet, int r, int c0, int c1, String text, XSSFCellStyle style) {
        for (int c = c0; c <= c1; c++) put(sheet, r, c, null, style);
        put(sheet, r, c0, text, style);
        if (c1 > c0) sheet.addMergedRegion(new CellRangeAddress(r, r, c0, c1));
    }

    private void fillRow(XSSFSheet sheet, int r, int c0, int c1, XSSFCellStyle style) {
        for (int c = c0; c <= c1; c++) put(sheet, r, c, null, style);
    }

    private void put(XSSFSheet sheet, int r, int c, Object val, XSSFCellStyle style) {
        Row row = sheet.getRow(r);
        if (row == null) row = sheet.createRow(r);
        Cell cell = row.getCell(c);
        if (cell == null) cell = row.createCell(c);
        if (val instanceof Number) cell.setCellValue(((Number) val).doubleValue());
        else if (val != null) cell.setCellValue(val.toString());
        if (style != null) cell.setCellStyle(style);
    }

    /** Grouped bar chart with one series per week column (C..), categories = car names (col B). */
    private void addBarChart(XSSFDrawing drawing, XSSFSheet sheet, int topRow, int dataStart, int dataEnd,
                             int nw, int lastCol, String title) {
        if (dataEnd < dataStart || nw <= 0) return; // nothing to plot
        try {
            int anchorCol = nw + 4;                       // start to the right of the table (B..Cumulative)
            int c2 = Math.min(anchorCol + 9, lastCol);
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, anchorCol, topRow, c2, topRow + CHART_ROWS);
            XSSFChart chart = drawing.createChart(anchor);
            if (title != null) { chart.setTitleText(title); chart.setTitleOverlay(false); }
            chart.getOrAddLegend().setPosition(LegendPosition.BOTTOM);

            XDDFCategoryAxis bottom = chart.createCategoryAxis(AxisPosition.BOTTOM);
            XDDFValueAxis left = chart.createValueAxis(AxisPosition.LEFT);
            XDDFDataSource<String> cats = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                    new CellRangeAddress(dataStart, dataEnd, CAT_COL, CAT_COL));

            XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottom, left);
            data.setBarDirection(BarDirection.COL);
            int[][] palette = {{0x15, 0x60, 0x82}, {0xE9, 0x71, 0x32}, {0x19, 0x6B, 0x24},
                    {0x0F, 0x9E, 0xD5}, {0xA0, 0x2B, 0x93}, {0x4E, 0xA7, 0x2E}};
            Row header = sheet.getRow(dataStart - 1);
            for (int i = 0; i < nw; i++) {
                int col = 2 + i; // C, D, ...
                XDDFNumericalDataSource<Double> vals = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                        new CellRangeAddress(dataStart, dataEnd, col, col));
                XDDFChartData.Series ser = data.addSeries(cats, vals);
                String label = (header != null && header.getCell(col) != null)
                        ? header.getCell(col).getStringCellValue() : ("W" + (i + 1));
                ser.setTitle(label, null);
                int[] rgb = palette[i % palette.length];
                setSeriesColor(ser, rgb[0], rgb[1], rgb[2]);
            }
            chart.plot(data);
        } catch (Exception e) {
            log.warn("Skipping chart ({}): {}", title, e.getMessage());
        }
    }

    private void setSeriesColor(XDDFChartData.Series series, int r, int g, int b) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(
                XDDFColor.from(new byte[]{(byte) r, (byte) g, (byte) b}));
        XDDFShapeProperties props = series.getShapeProperties();
        if (props == null) props = new XDDFShapeProperties();
        props.setFillProperties(fill);
        series.setShapeProperties(props);
    }

    private void applyColumnWidths(XSSFSheet sheet, int lastCol, int periodLabelCol) {
        for (int c = 1; c <= lastCol; c++) {
            // B wide; Period label wide; the rest (incl. week columns) wide enough to show a full
            // "dd MMM - dd MMM" date range without clipping.
            double w = (c == 1) ? 39.4 : (c == periodLabelCol) ? 33.1 : 18.0;
            sheet.setColumnWidth(c, (int) (w * 256));
        }
    }

    // ----- cell-style factory -----

    private Map<String, XSSFCellStyle> buildStyles(XSSFWorkbook wb) {
        String AP = "Aptos Narrow", MO = "MontserratRegular";
        String BLACK = "000000", DATA = "212529";
        Map<String, XSSFCellStyle> m = new HashMap<>();
        m.put("title", base(wb, AP, 24, true, "A6CAEC", BLACK, HorizontalAlignment.CENTER, null));
        m.put("bandBig", base(wb, AP, 20, true, "F6C6AD", BLACK, HorizontalAlignment.LEFT, null));
        m.put("band", base(wb, AP, 16, true, "F2AA84", BLACK, HorizontalAlignment.LEFT, null));
        XSSFCellStyle yellow = base(wb, AP, 12, true, "FFFF00", BLACK, HorizontalAlignment.CENTER, null);
        yellow.setBorderBottom(BorderStyle.HAIR);
        m.put("yellowLabel", yellow);
        // Table column headings (COUNTRY, Last week, Cumulative, Period, week labels, …) at 12pt.
        XSSFCellStyle hdrLeft = base(wb, AP, 12, true, "83CBEB", BLACK, HorizontalAlignment.LEFT, null);
        allHair(hdrLeft);
        m.put("hdrLeft", hdrLeft);
        XSSFCellStyle hdrRight = base(wb, AP, 12, true, "83CBEB", BLACK, HorizontalAlignment.RIGHT, null);
        allHair(hdrRight);
        m.put("hdrRight", hdrRight);
        m.put("periodHdr", base(wb, AP, 12, true, "83CBEB", BLACK, HorizontalAlignment.LEFT, null));
        // Weekly date-range headings: 12pt, centered and wrap-enabled so labels like "21 Sep - 27 Sep"
        // stay fully visible (never clipped by the next filled cell) in the week columns.
        XSSFCellStyle weekHdr = base(wb, AP, 12, true, "83CBEB", BLACK, HorizontalAlignment.CENTER, null);
        weekHdr.setWrapText(true);
        allHair(weekHdr);
        m.put("weekHdr", weekHdr);
        m.put("kpiVal", base(wb, MO, 12, false, null, DATA, HorizontalAlignment.CENTER, null));
        m.put("ctyName", base(wb, AP, 14, false, null, BLACK, HorizontalAlignment.LEFT, null));
        XSSFCellStyle gtLabel = base(wb, AP, 12, true, "C1E5F5", BLACK, HorizontalAlignment.LEFT, null);
        dotted(gtLabel);
        m.put("gtLabel", gtLabel);
        m.put("gtNum", base(wb, AP, 12, true, "C1E5F5", BLACK, HorizontalAlignment.RIGHT, "#,##0"));
        m.put("num", base(wb, MO, 10.5, false, null, DATA, HorizontalAlignment.RIGHT, "#,##0"));
        m.put("str", base(wb, MO, 10.5, false, null, DATA, HorizontalAlignment.RIGHT, null));
        m.put("carName", base(wb, AP, 12, false, "BFBFBF", BLACK, HorizontalAlignment.LEFT, null));
        m.put("sub", base(wb, AP, 12, true, "C1E5F5", BLACK, HorizontalAlignment.CENTER, null));
        XSSFCellStyle carsHdr = base(wb, AP, 12, true, "C1E5F5", BLACK, HorizontalAlignment.LEFT, null);
        carsHdr.setBorderBottom(BorderStyle.THIN);
        m.put("carsHdr", carsHdr);
        m.put("gtAccentNum", base(wb, MO, 10.5, true, "CAEEFB", DATA, HorizontalAlignment.RIGHT, "#,##0"));
        m.put("gtAccentStr", base(wb, MO, 10.5, true, "CAEEFB", DATA, HorizontalAlignment.RIGHT, null));
        m.put("storeYellow", base(wb, AP, 16, true, "FFFF00", BLACK, HorizontalAlignment.LEFT, null));
        return m;
    }

    private XSSFCellStyle base(XSSFWorkbook wb, String font, double size, boolean bold,
                               String fillHex, String fontHex, HorizontalAlignment ha, String fmt) {
        XSSFCellStyle st = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setFontName(font);
        f.setFontHeight((short) Math.round(size * 20));
        f.setBold(bold);
        if (fontHex != null) f.setColor(xc(fontHex));
        st.setFont(f);
        if (fillHex != null) {
            st.setFillForegroundColor(xc(fillHex));
            st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (ha != null) st.setAlignment(ha);
        st.setVerticalAlignment(VerticalAlignment.CENTER);
        if (fmt != null) st.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat(fmt));
        return st;
    }

    private XSSFColor xc(String hex) {
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new XSSFColor(new byte[]{(byte) r, (byte) g, (byte) b}, null);
    }

    private void allHair(XSSFCellStyle st) {
        st.setBorderTop(BorderStyle.HAIR);
        st.setBorderBottom(BorderStyle.HAIR);
        st.setBorderLeft(BorderStyle.HAIR);
        st.setBorderRight(BorderStyle.HAIR);
    }

    private void dotted(XSSFCellStyle st) {
        st.setBorderTop(BorderStyle.DOTTED);
        st.setBorderBottom(BorderStyle.DOTTED);
        st.setBorderLeft(BorderStyle.DOTTED);
        st.setBorderRight(BorderStyle.DOTTED);
    }

    // ----- data extraction / lookup helpers -----

    private List<Map<String, Object>> listFromResponse(ApplicationResponse resp, String key) {
        if (resp == null || resp.getData() == null) return Collections.emptyList();
        Object data = resp.getData();
        if (key != null && data instanceof Map) data = ((Map<?, ?>) data).get(key);
        return toMapList(data);
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<Map<String, Object>>> graphFromResponse(ApplicationResponse resp) {
        Map<String, List<Map<String, Object>>> out = new LinkedHashMap<>();
        if (resp == null || !(resp.getData() instanceof Map)) return out;
        Object graph = ((Map<String, Object>) resp.getData()).get("graphData");
        if (graph instanceof Map) {
            for (Map.Entry<?, ?> e : ((Map<?, ?>) graph).entrySet()) {
                out.put(String.valueOf(e.getKey()), toMapList(e.getValue()));
            }
        }
        return out;
    }

    private List<Map<String, Object>> toMapList(Object data) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (data instanceof List<?>) {
            for (Object obj : (List<?>) data) result.add(objToMap(obj));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> objToMap(Object obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (obj == null) return map;
        if (obj instanceof Map) {
            map.putAll((Map<String, Object>) obj);
            return map;
        }
        for (Class<?> cls = obj.getClass(); cls != null && cls != Object.class; cls = cls.getSuperclass()) {
            for (Field field : cls.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    map.putIfAbsent(field.getName(), field.get(obj));
                } catch (IllegalAccessException ignored) {}
            }
        }
        return map;
    }

    private void collectCountries(LinkedHashMap<String, String> countries, List<Map<String, Object>> list) {
        for (Map<String, Object> row : list) {
            String code = str(row.get("country"));
            if (code != null && !code.isEmpty()) countries.putIfAbsent(code, code);
        }
    }

    private String storeCountryName(List<Map<String, Object>> list, String code) {
        if (list != null && !list.isEmpty()) {
            String n = str(list.get(0).get("countryName"));
            if (n != null && !n.isEmpty()) return n;
        }
        return code;
    }

    private boolean rowMatches(Map<String, Object> row, String code, String name) {
        for (String f : new String[]{"country", "countryCode", "countryName"}) {
            Object v = row.get(f);
            if (v != null) {
                String sv = v.toString();
                if ((code != null && sv.equalsIgnoreCase(code)) || (name != null && sv.equalsIgnoreCase(name))) {
                    return true;
                }
            }
        }
        return false;
    }

    private Map<String, Object> findRow(List<Map<String, Object>> list, String code, String name) {
        for (Map<String, Object> row : list) {
            if (rowMatches(row, code, name)) return row;
        }
        return Collections.emptyMap();
    }

    private List<Map<String, Object>> filterByCountry(List<Map<String, Object>> list, String code, String name) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> row : list) {
            if (rowMatches(row, code, name)) out.add(row);
        }
        return out;
    }

    private double storeTotal(List<Map<String, Object>> list) {
        if (list == null) return 0;
        double total = 0;
        for (Map<String, Object> row : list) total += toDouble(row.get("downloadCount"));
        return total;
    }

    // ----- weekly bundling -----

    /** All dashboard datasets for one date range (full range, or a single week). */
    private static final class DataBundle {
        List<Map<String, Object>> appLaunch, brand, averages, cars, rc, race;
        Map<String, List<Map<String, Object>>> appStore, playStore;
    }

    /** Runs the same services the dashboard screen uses, scoped to the given date range. */
    private DataBundle gather(Map<String, String> fd) {
        return gather(fd, true);
    }

    /**
     * Runs the dashboard services for the given date range. {@code includeStores} controls whether
     * the App Store / Play Store datasets are fetched: the per-week bundles of the weekly report do
     * not use them (the Store Performance section is not rendered), so skipping those two queries
     * for every week avoids a large amount of redundant DB work and speeds the weekly export up.
     */
    private DataBundle gather(Map<String, String> fd, boolean includeStores) {
        DataBundle b = new DataBundle();
        try {
            b.appLaunch = listFromResponse(appLaunchedService.getCountryWiseData(fd), "summaryTable");
            b.brand = listFromResponse(brandViewedGarageService.getCountryWiseData(fd), "summaryTable");
            b.averages = listFromResponse(carUnlockedService.getCountryWiseAverages(fd), null);
            b.cars = listFromResponse(carUnlockedService.getCarUnlockedData(fd), "summary");
            b.rc = listFromResponse(rcPlayedService.getCountryWiseData(fd), "summary");
            b.race = listFromResponse(racePlayedService.getCountryWiseData(fd), "summary");
            if (includeStores) {
                b.appStore = graphFromResponse(appStoreSalesServiceImpl.fetchAppStoreSalesReport(fd));
                b.playStore = graphFromResponse(playStoreReportServiceImpl.fetchPlayStoreSalesReport(fd));
            }
        } catch (Exception e) {
            log.warn("Data gather failed for range {}..{}: {}", fd.get("startDate"), fd.get("endDate"), e.getMessage());
        }
        if (b.appLaunch == null) b.appLaunch = Collections.emptyList();
        if (b.brand == null) b.brand = Collections.emptyList();
        if (b.averages == null) b.averages = Collections.emptyList();
        if (b.cars == null) b.cars = Collections.emptyList();
        if (b.rc == null) b.rc = Collections.emptyList();
        if (b.race == null) b.race = Collections.emptyList();
        if (b.appStore == null) b.appStore = Collections.emptyMap();
        if (b.playStore == null) b.playStore = Collections.emptyMap();
        return b;
    }

    /** A 7-day bucket of the report range, e.g. "21 Sep - 27 Sep". */
    private record Week(String start, String end, String label) {}

    /** Splits [startIso, endIso] into consecutive 7-day weeks (label uses the full 7-day span). */
    private List<Week> computeWeeks(String startIso, String endIso) {
        List<Week> weeks = new ArrayList<>();
        try {
            LocalDate start = LocalDate.parse(startIso.trim());
            LocalDate end = LocalDate.parse(endIso.trim());
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH);
            // Weeks align to calendar Mon–Sun: the first week runs from the selected start date
            // to that week's Sunday (so a mid-week start yields a short first column), each later
            // week runs Monday–Sunday, and the last week is capped at the selected end date.
            LocalDate ws = start;
            while (!ws.isAfter(end)) {
                LocalDate sunday = ws.plusDays(7 - ws.getDayOfWeek().getValue()); // end of ws's calendar week
                LocalDate weekEnd = sunday.isAfter(end) ? end : sunday;
                weeks.add(new Week(ws.toString(), weekEnd.toString(), ws.format(f) + " - " + weekEnd.format(f)));
                ws = weekEnd.plusDays(1); // next week starts the day after this week's end (a Monday)
            }
        } catch (Exception e) {
            log.warn("Week computation failed for {}..{}: {}", startIso, endIso, e.getMessage());
        }
        return weeks;
    }

    /** Ordered distinct car names for one country from a cars/rc/race summary list. */
    private List<String> carOrder(List<Map<String, Object>> list, String code, String name) {
        List<String> out = new ArrayList<>();
        for (Map<String, Object> row : filterByCountry(list, code, name)) {
            String cn = str(row.get("carName"));
            if (cn != null && !out.contains(cn)) out.add(cn);
        }
        return out;
    }

    private double carField(List<Map<String, Object>> list, String code, String name, String carName, String field) {
        for (Map<String, Object> row : filterByCountry(list, code, name)) {
            if (carName.equals(str(row.get("carName")))) return toDouble(row.get(field));
        }
        return 0;
    }

    private String carFieldStr(List<Map<String, Object>> list, String code, String name, String carName, String field) {
        for (Map<String, Object> row : filterByCountry(list, code, name)) {
            if (carName.equals(str(row.get("carName")))) {
                String v = str(row.get(field));
                return v != null ? v : "";
            }
        }
        return "";
    }

    private double sumField(List<Map<String, Object>> list, String field) {
        double total = 0;
        for (Map<String, Object> row : list) total += toDouble(row.get(field));
        return total;
    }

    private double roundSumField(List<Map<String, Object>> list, String field) {
        double total = 0;
        for (Map<String, Object> row : list) total += Math.round(toDouble(row.get(field)));
        return total;
    }

    // ----- value formatting -----

    private String str(Object o) {
        return o == null ? null : o.toString();
    }

    private double toDouble(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try {
            return Double.parseDouble(o.toString().replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String fmt2(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    private String formatDuration(double minutes) {
        if (Double.isNaN(minutes)) return "-";
        long hrs = (long) Math.floor(minutes / 60);
        long mins = Math.round(minutes % 60);
        if (hrs > 0 && mins > 0) return hrs + " h " + mins + " min";
        if (hrs > 0) return hrs + " h 0 min";
        return "0 h " + mins + " min";
    }

    private String formatK(double v) {
        if (v >= 1000) return String.format(Locale.US, "%.2fK", v / 1000.0);
        return String.valueOf(Math.round(v));
    }

    private String formatPeriod(String isoDate) {
        if (isoDate == null || isoDate.trim().isEmpty()) return "";
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(isoDate.trim());
            return new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH).format(d).toUpperCase(Locale.ENGLISH);
        } catch (ParseException e) {
            return isoDate;
        }
    }

    private String uniqueSheetName(Set<String> used, String name) {
        String base = ("DASHBOARD-" + (name == null ? "" : name)).toUpperCase(Locale.ENGLISH);
        base = base.replaceAll("[\\\\/?*\\[\\]:]", " ").trim();
        if (base.length() > 31) base = base.substring(0, 31);
        String candidate = base;
        int i = 2;
        while (!used.add(candidate)) {
            String suffix = "-" + i++;
            candidate = (base.length() + suffix.length() > 31 ? base.substring(0, 31 - suffix.length()) : base) + suffix;
        }
        return candidate;
    }
}



