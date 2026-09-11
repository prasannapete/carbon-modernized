package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Response;

import lombok.Data;

@Data
public class ClientAccessTokenResponse {
    public String access_token;
    public String expires_in;
    public String token_type;
}
