package com.pcpl.carbon.kitkat_service.KitkatEvents.KkRacePlayed.Service;

import com.pcpl.carbon.kitkat_service.KitkatEvents.KkRacePlayed.DTO.KkRacePlayedRaceInfoDTO;
import com.pcpl.carbon.kitkat_service.KitkatEvents.KkRacePlayed.Repository.KkRacePlayedRepository;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.Kitkat.KkRacePlayed.DTO.KkRacePlayedDTO;
import com.pcpl.carbon.pcplsdk.Kitkat.KkRacePlayed.Model.KkRacePlayed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KkRacePlayedServiceImpl extends AbstractLazyService<KkRacePlayed, KkRacePlayedDTO, KkRacePlayedRepository> implements KkRacePlayedService {
    @Autowired
    KkRacePlayedRepository kkRacePlayedRepository;

    @Override
    public KkRacePlayed getEntityObject() {
        return new KkRacePlayed();
    }

    @Override
    public KkRacePlayedDTO getDtoObject() {
        return new KkRacePlayedDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }

    @Override
    public ApplicationResponse getRaceInfo(Integer consoleId) {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            List<KkRacePlayedRaceInfoDTO> raceInfo = getRaceInfoDTOs(consoleId);

            if (consoleId == null) {
                Map<String, List<KkRacePlayedRaceInfoDTO>> groupedRaceInfo = raceInfo.stream()
                        .collect(Collectors.groupingBy(
                                race -> race.getConsoleId() == null ? "UNKNOWN" : String.valueOf(race.getConsoleId()),
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));
                applicationResponse.setData(groupedRaceInfo);
            } else {
                applicationResponse.setData(raceInfo);
            }

            applicationResponse.setSuccess(true);
            applicationResponse.setMessage("Race info fetched successfully.");
        } catch (Exception ex) {
            applicationResponse.setSuccess(false);
            applicationResponse.setError(ex.getMessage());
        }
        return applicationResponse;
    }

    private List<KkRacePlayedRaceInfoDTO> getRaceInfoDTOs(Integer consoleId) {
        List<KkRacePlayed> racePlayed = consoleId == null
                ? kkRacePlayedRepository.findAllByIsDeleted(0)
                : kkRacePlayedRepository.findAllByConsoleIdAndIsDeleted(consoleId, 0);

        return racePlayed.stream()
                .sorted(Comparator
                        .comparing(KkRacePlayed::getConsoleId, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(KkRacePlayed::getRaceDuration, Comparator.nullsLast(Float::compareTo)))
                .map(race -> new KkRacePlayedRaceInfoDTO(race, formatSeconds(race.getRaceDuration())))
                .collect(Collectors.toList());
    }

    private String formatSeconds(Float durationInSeconds) {
        if (durationInSeconds == null || durationInSeconds < 0) {
            return "00:00:00";
        }

        long totalSeconds = Math.round(durationInSeconds);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
