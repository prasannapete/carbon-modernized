package com.pcpl.carbon.pcplsdk.Leaderboards.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardsDTO {
    private Long id;
    private String leaderboardName;
    private String leaderboardInternalName;
    private String leaderboardId;
    private int isActive;
    private Long tenantId;

}
