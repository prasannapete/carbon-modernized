package com.pcpl.carbon.pcplsdk.Leaderboards.Response;

import lombok.Data;

@Data
public class LoginResponse {
    private int code;
    private String status;
    private Data1 data;

//    @lombok.Data
//    public static class Data {
//        private EntityToken EntityToken;
//        private String PlayFabId;
//        private String SessionTicket;
//        private boolean NewlyCreated;
//        private Entity Entity;
//    }

//    @lombok.Data
//    public static class EntityToken {
//        private String EntityToken;
//        private String TokenExpiration;
//        private EntityDetails Entity;
//    }

//    @lombok.Data
//    public static class EntityDetails {
//        private String Id;
//        private String Type;
//        private String TypeString;
//    }

//    @lombok.Data
//    public static class Entity {
//        private String Id;
//        private String Type;
//        private String TypeString;
//    }
}
