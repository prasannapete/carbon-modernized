package com.pcpl.carbon.shell_analytics_service.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties("com.pcpl.carbon")
@Data
@Slf4j
public class SrlAnalyticsConfig {
    public String accessTokenUrl;
    public String ssoClientId;
    public String oauthClientId;
    public String oauthClientSecret;
    public String oauthGrantType;
    public String application;
    public String channel;
    public String p8FilePath;
    public String keyId;
    public String issuerId;
    public String appId;
    public String playstoreJsonFilePath;
    public String vendorNumber;
    public String fileUploadPath;
    public String mailFromEmailId;
    public String appBaseUrl;
    public int mailClientType;
    public String baseTokenURL;
    public String clientTokenUrl;
    public String assignOfferURL;
    public String exchangeAccessCodeURL;
    public String userTokenURL;
    public String playMobilBaseUrl;
}
