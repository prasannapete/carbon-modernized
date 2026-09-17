package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Scheduler;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Service.ShellEventsService;
import com.pcpl.carbon.shell_analytics_service.config.SrlAnalyticsConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class ReportUpdateScheduler {
    @Autowired
    ShellEventsService shellEventsService;

    @Autowired
    private SrlAnalyticsConfig srlAnalyticsConfig;

    @Scheduled(cron = "0 00 00 * * ?") // Runs every day at 12:00 PM
    public void executeDailyTask() throws Exception {
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
                .signWith(ecPrivateKey, SignatureAlgorithm.ES256)
                .compact();

        log.trace("Entering");
        ApplicationResponse appReviewsResponse = ApplicationResponse.builder().build();
        try {
            appReviewsResponse = shellEventsService.fetchSaleReport(jwtToken,null);

            appReviewsResponse.setSuccess(true);
            appReviewsResponse.setError("");

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            appReviewsResponse.setSuccess(false);
            appReviewsResponse.setError(ex.getMessage());
        }

        log.trace("Exiting");
    }

    private static PrivateKey getPrivateKeyFromP8File(InputStream filePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes =filePath.readAllBytes();
        String privateKeyContent = new String(keyBytes);
        privateKeyContent = privateKeyContent.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(privateKeyContent);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }
}
