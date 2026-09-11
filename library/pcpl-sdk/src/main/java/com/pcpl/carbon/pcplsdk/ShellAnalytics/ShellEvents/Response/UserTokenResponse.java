package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Response;

import lombok.Data;

@Data
public class UserTokenResponse {
    public String accessToken;
    public String refreshToken;
    public Integer expiresIn;
}
