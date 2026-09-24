package com.pcpl.carbon.authserver.AppFeature.Repository;

import com.pcpl.carbon.authserver.AppFeature.Model.AppFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppFeatureRepository  extends JpaRepository<AppFeature, Long> {

    // ---- CRUD / tenant-scoped lookups (soft-delete aware) ----
    Optional<AppFeature> findByIdAndIsDeleted(Long id, int isDeleted);

    List<AppFeature> findAllByIsDeletedOrderBySequenceAsc(int isDeleted);

    List<AppFeature> findAllByTenantIdAndIsDeletedOrderBySequenceAsc(Long tenantId, int isDeleted);

    @Query(
            value = "SELECT new com.pcpl.carbon.authserver.AppFeature.Model.AppFeature(" +
                    " AF.id," +
                    " AF.featureName," +
                    " AF.toolTip," +
                    " AF.systemRole," +
                    " AF.url," +
                    " AF.sequence," +
                    " AF.isSystem," +
                    " AF.createdBy," +
                    " AF.creationTime," +
                    " AF.lastModifiedBy," +
                    " AF.lastModifiedTime," +
                    " AF.isDeleted," +
                    " AF.deletedBy," +
                    " AF.deletedTime" +
                    " ) " +
                    " FROM AppFeature as AF" +
                    " WHERE AF.id IN ( SELECT RG.appFeatureId FROM RoleGrant RG " +
                    " WHERE RG.roleId IN ( SELECT RA.roleId FROM RoleAccount RA WHERE RA.accountId  = :userId) )"
    )
    List<AppFeature> getAppFeaturesForUser(@Param("userId") Long userId);

}
