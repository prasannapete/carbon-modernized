package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferIssueDetailsDTO {
    private Long id;
    private String consumerUuid;
    private Integer eventType;
    private String offerCode;
    private Date issuedDate;
    private String countryCode;
    private String eventTypeText;
    private Integer points;
    private String gameId;
}
