package com.pcpl.carbon.pcplsdk.Leaderboards.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardsDTO {
    private long Id;
    private String leaderboardName;
    private String leaderboardInternalName;
    private int isActive;
    private Long tenantId;

}
