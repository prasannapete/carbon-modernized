package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Response;

import lombok.Data;

@Data
public class UserAccessTokenResponse {
    public String uuid;
    public Market market;
    public String accessToken;
    public String refreshToken;
    public Profile profile;
    public String firstName;
    public String lastName;
}
