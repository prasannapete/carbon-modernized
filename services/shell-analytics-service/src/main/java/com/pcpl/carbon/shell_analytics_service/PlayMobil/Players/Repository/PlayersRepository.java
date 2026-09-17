package com.pcpl.carbon.shell_analytics_service.PlayMobil.Players.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.PlayMobil.Players.Model.Players;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayersRepository extends JpaRepository<Players,String> {
    List<Players> findAllByIsDeleted(int isDeleted);
    List<Players> findAllByIsDeletedOrderByCreationTime(int isDeleted);

}
