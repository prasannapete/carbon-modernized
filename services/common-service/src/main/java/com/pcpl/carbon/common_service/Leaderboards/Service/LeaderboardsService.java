package com.pcpl.carbon.common_service.Leaderboards.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;

public interface LeaderboardsService {

    ApplicationResponse get(String statisticName, Integer startPosition) throws Exception;

}
