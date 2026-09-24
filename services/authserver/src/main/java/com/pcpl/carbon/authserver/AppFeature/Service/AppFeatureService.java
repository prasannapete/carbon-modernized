package com.pcpl.carbon.authserver.AppFeature.Service;



import com.pcpl.carbon.authserver.AppFeature.DTO.AppFeatureDTO;
import com.pcpl.carbon.authserver.AppFeature.Model.AppFeature;

import java.util.List;

public interface AppFeatureService {

    List<AppFeature> getAppFeaturesForUser(Long userId) throws Exception;

    /** Create or update an AppFeature (update when the DTO carries an id). Stamps {@code tenantId}. */
    AppFeatureDTO saveAppFeature(AppFeatureDTO dto, Long tenantId) throws Exception;

    /** Get a single non-deleted AppFeature by id, or {@code null} when not found. */
    AppFeatureDTO getAppFeatureById(Long id) throws Exception;

    /** Get non-deleted AppFeatures, scoped to {@code tenantId} when it is non-null. */
    List<AppFeatureDTO> getAllAppFeatures(Long tenantId) throws Exception;

    /** Soft-delete an AppFeature (isDeleted/deletedBy/deletedTime); returns the deleted DTO or null. */
    AppFeatureDTO deleteAppFeature(Long id, Long deletedBy) throws Exception;
}
