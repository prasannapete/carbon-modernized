package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.ShellEventsDataDTO;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Service.ShellEventsService;
import com.pcpl.carbon.shell_analytics_service.config.ApplicationContextProvider;
import com.pcpl.carbon.shell_analytics_service.config.SrlAnalyticsConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;


@RestController
@RequestMapping(value = "/shell-events")
@Slf4j
@Component
public class ShellEventsController {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private  RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    ShellEventsService shellEventsService;
    @Autowired
    private SrlAnalyticsConfig srlAnalyticsConfig;

    @RequestMapping(value = "/save-events", method = RequestMethod.POST)
    public ApplicationResponse saveData(@RequestBody ShellEventsDataDTO shellEventsDataDTO) {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        applicationResponse = shellEventsService.saveObject("shell_events",shellEventsDataDTO);
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

    @RequestMapping(value = "/get-events", method = RequestMethod.POST)
    public ApplicationResponse getEventData(@RequestBody String key) {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        applicationResponse = shellEventsService.getObject("shell_events");
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }


    @RequestMapping(value = "/test-redis", method = RequestMethod.POST)
    public static ApplicationResponse redisFunction() {
        RedisConnectionFactory redisConnectionFactory =
                ApplicationContextProvider.getContext().getBean(RedisConnectionFactory.class);
            ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            if (connection.ping() != null) {
                applicationResponse.setMessage("Connected to Redis successfully!");
            } else {
                applicationResponse.setMessage("Failed to connect to Redis.");
            }
        } catch (Exception e) {
            applicationResponse.setError("Redis connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return applicationResponse;
    }

    @RequestMapping(value = "/fetch-app-store-reports", method = RequestMethod.POST)
    public @ResponseBody ApplicationResponse generateToken(@RequestBody Map<String,String> formData) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("SRL-api-key/SRL-api-key/AuthKey_76C22635M8.p8");
        PrivateKey privateKey = getPrivateKeyFromP8File(inputStream);
        ECPrivateKey ecPrivateKey = (ECPrivateKey) privateKey;
        System.out.println("Key Algorithm: " + privateKey.getAlgorithm());
        String jwtToken = Jwts.builder()
                .setHeaderParam("alg", "ES256")
                .setHeaderParam("kid", srlAnalyticsConfig.getKeyId())
                .setHeaderParam("typ", "JWT")
                .setIssuer(srlAnalyticsConfig.getIssuerId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .setAudience("appstoreconnect-v1")
                .signWith(ecPrivateKey,SignatureAlgorithm.ES256)
                .compact();

        log.trace("Entering");
        ApplicationResponse appReviewsResponse = ApplicationResponse.builder().build();
        try {
            appReviewsResponse = shellEventsService.fetchSaleReport(jwtToken,formData.get("date"));

            appReviewsResponse.setSuccess(true);
            appReviewsResponse.setError("");

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            appReviewsResponse.setSuccess(false);
            appReviewsResponse.setError(ex.getMessage());
        }

        log.trace("Exiting");
        return appReviewsResponse;
    }

    @RequestMapping(value = "/fetch-play-store-reports", method = RequestMethod.POST)
    public @ResponseBody ApplicationResponse fetchPlayStoreReport(){

        ApplicationResponse appReviewsResponse = ApplicationResponse.builder().build();
        try {
            appReviewsResponse = shellEventsService.playStoreReport();

            appReviewsResponse.setSuccess(true);
            appReviewsResponse.setError("");

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            appReviewsResponse.setSuccess(false);
            appReviewsResponse.setError(ex.getMessage());
        }

        log.trace("Exiting");
        return appReviewsResponse;
    }

    @RequestMapping(value = "/upload-csv-file", method = RequestMethod.POST)
    public ApplicationResponse uploadImage(
            @RequestParam("file") MultipartFile[] uploadedfiles
    ) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            for (MultipartFile file : uploadedfiles) {
                applicationResponse = shellEventsService.importPlayStoreDataByCSV(file);
            }
        } catch (Exception e) {
            applicationResponse.setSuccess(false);
            applicationResponse.setError(e.getLocalizedMessage());
            return applicationResponse;
        }
        log.info("Exiting");
        return applicationResponse;
    }

    private static PrivateKey getPrivateKeyFromP8File(InputStream filePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = filePath.readAllBytes();
        String privateKeyContent = new String(keyBytes);
        privateKeyContent = privateKeyContent.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(privateKeyContent);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }


    @RequestMapping(value = "/export-multi-tab-excel", method = RequestMethod.POST)
    public ApplicationResponse exportMultiTabExcel(HttpServletResponse response,
                                    @RequestBody Map<String, String> request) throws Exception {
        String startDate = request.get("startDate");
        String endDate = request.get("endDate");
        String country = request.get("country");

        try {
            shellEventsService.exportMultiTabExcel(response, startDate, endDate,country);
        } catch (Exception e) {
            e.printStackTrace();
            response.reset();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Excel generation failed");
        }
        return ApplicationResponse.builder().build();
    }
}
