package com.pcpl.carbon.pcplsdk.Leaderboards.Response;

import lombok.Data;
@Data
public class Data1 {
    public String SessionTicket;
    public String PlayFabId;
    public boolean NewlyCreated;
    public SettingsForUser SettingsForUser;
    public String LastLoginTime;
    public EntityToken EntityToken;
    public TreatmentAssignment TreatmentAssignment;
}

