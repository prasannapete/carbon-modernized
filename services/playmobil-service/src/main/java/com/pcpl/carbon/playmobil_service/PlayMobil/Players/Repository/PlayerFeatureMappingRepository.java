package com.pcpl.carbon.playmobilservice.PlayMobil.Players.Repository;

import com.pcpl.carbon.pcplsdk.PlayMobil.Players.Model.PlayerFeatureMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerFeatureMappingRepository extends JpaRepository<PlayerFeatureMapping, Long> {
    Optional<PlayerFeatureMapping> findFirstByFeatureTypeIdIgnoreCaseAndFeatureCodeIgnoreCaseAndIsDeleted(
            String featureTypeId,
            String featureCode,
            int isDeleted
    );

    Optional<PlayerFeatureMapping> findFirstByFeatureCodeIgnoreCaseAndIsDeleted(String featureCode, int isDeleted);

    List<PlayerFeatureMapping> findAllByIsDeletedOrderByIdAsc(int isDeleted);
}
