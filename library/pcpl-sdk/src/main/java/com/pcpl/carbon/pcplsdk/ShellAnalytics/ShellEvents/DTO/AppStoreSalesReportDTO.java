package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppStoreSalesReportDTO {
    private Long tenantId;
    private String provider;
    private String providerCountry;
    private String sku;
    private String developer;
    private String title;
    private String version;
    private String productTypeIdentifier;
    private int units;
    private double developerProceeds;
    private Date beginDate;
    private Date endDate;
    private String customerCurrency;
    private String countryCode;
    private String currencyOfProceeds;
    private String appleIdentifier;
    private double customerPrice;
    private String promoCode;
    private String parentIdentifier;
    private String subscription;
    private String period;
    private String category;
    private String cmb;
    private String device;
    private String supportedPlatforms;
    private String proceedsReason;
    private String preservedPricing;
    private String client;
    private String orderType;
}
