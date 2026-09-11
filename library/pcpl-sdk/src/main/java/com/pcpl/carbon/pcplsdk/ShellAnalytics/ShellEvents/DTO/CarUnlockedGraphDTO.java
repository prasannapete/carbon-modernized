package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class CarUnlockedGraphDTO {
    private String country;
    private String carName;
    private Date date;
    private long unlockedCount;
    private long cumulativeCount;
    private String countryName;
    private Integer isActive;

    public CarUnlockedGraphDTO(String country, String carName, Date date, long unlockedCount, long cumulativeCount, String countryName, Integer isActive) {
        this.country = country;
        this.carName = carName;
        this.date = date;
        this.unlockedCount = unlockedCount;
        this.cumulativeCount = cumulativeCount;
        this.countryName = countryName;
        this.isActive = isActive;
    }
    public CarUnlockedGraphDTO(String country, String carName, long unlockedCount, long cumulativeCount, String countryName, Integer isActive) {
        this.country = country;
        this.carName = carName;
        this.unlockedCount = unlockedCount;
        this.cumulativeCount = cumulativeCount;
        this.countryName = countryName;
        this.isActive = isActive;
    }
}
