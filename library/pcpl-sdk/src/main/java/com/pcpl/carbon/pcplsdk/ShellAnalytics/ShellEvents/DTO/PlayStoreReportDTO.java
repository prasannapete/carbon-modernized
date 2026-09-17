package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayStoreReportDTO {
    private Long id;
    private Long tenantId;
    private Date date;
    private String country;
    private String countryCode;
    private long installs;

    public PlayStoreReportDTO(Date date, String country, long installs) {
        this.date = date;
        this.country = country;
        this.installs = installs;
    }
}
