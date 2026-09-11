package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppStoreSalesReportGraphDTO {
    private String country;
    private Integer month;
    private Integer year;
    private long downloadCount;
    private String monthName;
    private String countryName;

    public AppStoreSalesReportGraphDTO(String country, Integer month, Integer year, long downloadCount,String countryName) {
        this.country = country;
        this.month = month;
        this.year = year;
        this.downloadCount = downloadCount;
        this.countryName = countryName;
    }
}
