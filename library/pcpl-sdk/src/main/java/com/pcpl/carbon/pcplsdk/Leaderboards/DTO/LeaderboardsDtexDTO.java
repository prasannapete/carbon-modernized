package com.pcpl.carbon.pcplsdk.Leaderboards.DTO;


import com.pcpl.carbon.pcplsdk.Leaderboards.Model.PlayerProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardsDtexDTO {

    private String playFabId;
    private String displayName;
    private long statValue;
    private int position;
    private PlayerProfile profile;

}
