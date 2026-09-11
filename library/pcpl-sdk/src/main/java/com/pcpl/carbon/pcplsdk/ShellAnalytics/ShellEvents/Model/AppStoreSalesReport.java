package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.util.Date;

@Entity
@Table(name = "srl_app_store_sales_report")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppStoreSalesReport extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_country")
    private String providerCountry;

    @Column(name = "sku")
    private String sku;
    @Column(name = "developer")
    private String developer;

    @Column(name = "title")
    private String title;

    @Column(name = "version")
    private String version;

    @Column(name = "product_type_identifier")
    private String productTypeIdentifier;

    @Column(name = "units")
    private Integer units;

    @Column(name = "developer_proceeds")
    private Double developerProceeds;

    @Column(name = "begin_date")
    private Date beginDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "customer_currency")
    private String customerCurrency;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "currency_of_proceeds")
    private String currencyOfProceeds;

    @Column(name = "apple_identifier")
    private String appleIdentifier;

    @Column(name = "customer_price")
    private Double customerPrice;

    @Column(name = "promo_code")
    private String promoCode;

    @Column(name = "parent_identifier")
    private String parentIdentifier;

    @Column(name = "subscription")
    private String subscription;

    @Column(name = "period")
    private String period;

    @Column(name = "category")
    private String category;

    @Column(name = "cmb")
    private String cmb;

    @Column(name = "device")
    private String device;

    @Column(name = "supported_platforms")
    private String supportedPlatforms;

    @Column(name = "proceeds_reason")
    private String proceedsReason;

    @Column(name = "preserved_pricing")
    private String preservedPricing;

    @Column(name = "client")
    private String client;

    @Column(name = "order_type")
    private String orderType;

}
