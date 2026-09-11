package com.pcpl.carbon.pcplsdk.Leaderboards.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerProfile {
        private String publisherId;
        private String titleId;
        private String playerId;
        private Long tenantId;
        private String displayName;
    }

