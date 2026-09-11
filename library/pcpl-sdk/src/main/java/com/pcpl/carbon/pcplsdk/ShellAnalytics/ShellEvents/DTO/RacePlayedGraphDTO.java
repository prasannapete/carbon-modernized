package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RacePlayedGraphDTO {
    private String country;
    private String carName;
    private Date date;
    private Double totalRcDuration;
    private double cumulativeRcDuration;
    private String countryName;
    private Integer isActive;
    private String totalRacDurationInHours;
    private String cumulativeRacDurationInHours;

    public RacePlayedGraphDTO(String country, String carName, Double totalRcDuration, double cumulativeRcDuration, String countryName,Integer isActive,String totalRacDurationInHours) {
        this.country = country;
        this.carName = carName;
        this.totalRcDuration = totalRcDuration;
        this.cumulativeRcDuration = cumulativeRcDuration;
        this.countryName = countryName;
        this.isActive=isActive;
        this.totalRacDurationInHours = totalRacDurationInHours;
    }
}
