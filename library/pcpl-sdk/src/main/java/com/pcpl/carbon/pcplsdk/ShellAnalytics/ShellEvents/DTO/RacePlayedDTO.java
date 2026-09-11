package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RacePlayedDTO {
    private Long id;
    private Long tenantId;
    private String carName;
    private int trackId;
    private int result;
    private boolean isCompetition;
    private float raceDuration;
    private int brandCount;
    private String gameId;
    private String country;
    private float lapDuration;
    private String raceInfo;

    public Boolean getIsCompetition() {
        return isCompetition;
    }

    public void setIsCompetition(Boolean isCompetition) {
        this.isCompetition = isCompetition;
    }
}
