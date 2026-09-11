package com.pcpl.carbon.pcplsdk.Leaderboards.Response;

import lombok.Data;

@Data
public class EntityToken {
    public String EntityToken;
    public String TokenExpiration;
    public Entity Entity;
}
