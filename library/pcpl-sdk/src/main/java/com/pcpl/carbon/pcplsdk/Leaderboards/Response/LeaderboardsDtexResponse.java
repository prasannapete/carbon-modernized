package com.pcpl.carbon.pcplsdk.Leaderboards.Response;


import com.pcpl.carbon.pcplsdk.Leaderboards.DTO.LeaderboardsDtexDTO;
import lombok.Data;

import java.util.List;

@Data
public class LeaderboardsDtexResponse {

    private LeaderboardsDtexDTO Sos;
    private List<LeaderboardsDtexDTO> data;
    private long totalPages;
    private long recordsTotal;
    private long currentRecord;
    private long recordsFiltered;
    private boolean success;
    private String error;

}
