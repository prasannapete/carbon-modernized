package com.pcpl.carbon.playmobilservice.PlayMobil.Players.Repository;

import com.pcpl.carbon.pcplsdk.PlayMobil.Players.Model.Players;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayersRepository extends JpaRepository<Players,String> {
    List<Players> findAllByIsDeleted(int isDeleted);
    List<Players> findAllByIsDeletedOrderByCreationTime(int isDeleted);
    List<Players> findAllByTeamIgnoreCaseAndIsDeleted(String team, int isDeleted);
    Optional<Players> findFirstByOptaIdAndIsDeleted(String optaId, int isDeleted);
    List<Players> findAllByIsDeletedAndIsPublishedOrderByCreationTime(Integer isDeleted, Boolean isPublished);

}
