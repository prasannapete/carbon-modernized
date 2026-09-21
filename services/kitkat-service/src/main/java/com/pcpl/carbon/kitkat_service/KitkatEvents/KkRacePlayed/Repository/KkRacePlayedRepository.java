package com.pcpl.carbon.kitkat_service.KitkatEvents.KkRacePlayed.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.Kitkat.KkRacePlayed.Model.KkRacePlayed;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KkRacePlayedRepository extends PCPLCRUDRepository<KkRacePlayed> {
    List<KkRacePlayed> findAllByIsDeleted(int isDeleted);

    List<KkRacePlayed> findAllByConsoleIdAndIsDeleted(Integer consoleId, int isDeleted);
}
