package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferCodeConfigurationDTO {
    private Long id;
    private Long tenantId;
    private String countryCode;
    private String country;
    private Integer eventType;
    private String offerCode;
    private Date validFrom;
    private Date validUntil;
    private Integer isActive;
    private Integer points;
    private String eventTypeText;
    private Integer validityPeriod;
    private String gameId;
    private boolean isClaimed;
    private String langCode;
    private String eventTitle;
    private String eventReward;
    private String eventDescription;
    private String eventHowToClaim;

    public OfferCodeConfigurationDTO(Long id, String countryCode, String country, Integer eventType, String offerCode, Date validFrom, Date validUntil, Integer isActive, Integer points, String eventTypeText, Integer validityPeriod, String gameId, boolean isClaimed) {
        this.id = id;
        this.countryCode = countryCode;
        this.country = country;
        this.eventType = eventType;
        this.offerCode = offerCode;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.isActive = isActive;
        this.points = points;
        this.eventTypeText = eventTypeText;
        this.validityPeriod = validityPeriod;
        this.gameId = gameId;
        this.isClaimed = isClaimed;
    }
}
