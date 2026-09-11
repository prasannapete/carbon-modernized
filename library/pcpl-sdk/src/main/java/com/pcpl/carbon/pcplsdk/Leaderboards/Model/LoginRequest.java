package com.pcpl.carbon.pcplsdk.Leaderboards.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String CustomId;
    private boolean CreateAccount;
    private String TitleId;
    private Long TenantId;
}
