package com.pcpl.carbon.kitkat_service.KitkatEvents.KkRacePlayed.DTO;

import com.pcpl.carbon.pcplsdk.Kitkat.KkRacePlayed.Model.KkRacePlayed;
import lombok.Data;

import java.util.Date;

@Data
public class KkRacePlayedRaceInfoDTO {
    private Long id;
    private String gameId;
    private Integer consoleId;
    private String playerName;
    private String country;
    private String carName;
    private Integer trackId;
    private Integer result;
    private Float raceDuration;
    private String raceDurationFormatted;
    private Float lapDuration;
    private Integer brandCount;
    private String raceInfo;
    private Date creationTime;

    public KkRacePlayedRaceInfoDTO(KkRacePlayed racePlayed, String raceDurationFormatted) {
        this.id = racePlayed.getId();
        this.gameId = racePlayed.getGameId();
        this.consoleId = racePlayed.getConsoleId();
        this.playerName = racePlayed.getPlayerName();
        this.country = racePlayed.getCountry();
        this.carName = racePlayed.getCarName();
        this.trackId = racePlayed.getTrackId();
        this.result = racePlayed.getResult();
        this.raceDuration = racePlayed.getRaceDuration();
        this.raceDurationFormatted = raceDurationFormatted;
        this.lapDuration = racePlayed.getLapDuration();
        this.brandCount = racePlayed.getBrandCount();
        this.raceInfo = racePlayed.getRaceInfo();
        this.creationTime = racePlayed.getCreationTime();
    }
}
