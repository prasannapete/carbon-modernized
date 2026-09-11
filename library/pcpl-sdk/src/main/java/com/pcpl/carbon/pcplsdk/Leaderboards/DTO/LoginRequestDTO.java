package com.pcpl.carbon.pcplsdk.Leaderboards.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    private String CustomId;
    private boolean CreateAccount;
    private String TitleId;
    private Long TenantId;
}
