package com.pcpl.carbon.pcplsdk.Leaderboards.Response;

import com.pcpl.carbon.pcplsdk.Leaderboards.DTO.LeaderboardsDTO;

import lombok.Data;

import java.util.List;

@Data
public class LeaderboardsResponse {
    private LeaderboardsDTO Sos;
    private List<LeaderboardsDTO> data;
    private long totalPages;
    private long recordsTotal;
    private long currentRecord;
    private long recordsFiltered;
    private boolean success;
    private String error;
}
