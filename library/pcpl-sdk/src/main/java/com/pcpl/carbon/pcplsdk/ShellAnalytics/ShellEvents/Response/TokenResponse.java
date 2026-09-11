package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Response;

import lombok.Data;

@Data
public class TokenResponse {
    public String token;
    public String tokenType;
    public String expiresIn;
    public String owner;

}
