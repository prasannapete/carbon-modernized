package com.pcpl.carbon.authserver.AppFeature.Service;

import com.pcpl.carbon.authserver.AppFeature.DTO.AppFeatureDTO;
import com.pcpl.carbon.authserver.AppFeature.Model.AppFeature;
import com.pcpl.carbon.authserver.AppFeature.Repository.AppFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AppFeatureServiceImpl implements AppFeatureService {

    @Autowired
    AppFeatureRepository appFeatureRepository;

    @Override
    public List<AppFeature> getAppFeaturesForUser(Long userId) throws Exception {
        return appFeatureRepository.getAppFeaturesForUser(userId);
    }

    @Override
    @Transactional
    public AppFeatureDTO saveAppFeature(AppFeatureDTO dto, Long tenantId) throws Exception {
        AppFeature entity;
        boolean isNew = (dto.getId() == null);

        if (isNew) {
            entity = new AppFeature();
            entity.setCreationTime(new Date());
            entity.setCreatedBy(dto.getCreatedBy());
            entity.setIsDeleted(0);
        } else {
            // Update only the fields carried by the DTO; keep created/audit/soft-delete state.
            entity = appFeatureRepository.findByIdAndIsDeleted(dto.getId(), 0)
                    .orElseThrow(() -> new IllegalArgumentException("AppFeature not found for id: " + dto.getId()));
            entity.setLastModifiedTime(new Date());
            entity.setLastModifiedBy(dto.getLastModifiedBy());
        }

        entity.setFeatureName(dto.getFeatureName());
        entity.setToolTip(dto.getToolTip());
        entity.setSystemRole(dto.getSystemRole());
        entity.setUrl(dto.getUrl());
        entity.setSequence(dto.getSequence());
        entity.setIsSystem(dto.getIsSystem());

        // Tenant is taken from the request (subdomain) when available; on localhost/dev where the
        // request has no tenant sub-domain we fall back to the value in the body so it can be set.
        entity.setTenantId(tenantId != null ? tenantId : dto.getTenantId());

        // Parent/child: link the parent by id only (load the managed parent so the cascade on the
        // @ManyToOne does not try to re-insert it). Never recursively save parent/childrens.
        if (dto.getParentId() != null) {
            AppFeature parent = appFeatureRepository.findByIdAndIsDeleted(dto.getParentId(), 0)
                    .orElseThrow(() -> new IllegalArgumentException("Parent AppFeature not found for id: " + dto.getParentId()));
            entity.setParent(parent);
        } else {
            entity.setParent(null);
        }

        AppFeature saved = appFeatureRepository.save(entity);
        return toDto(saved, true);
    }

    @Override
    @Transactional(readOnly = true)
    public AppFeatureDTO getAppFeatureById(Long id) throws Exception {
        return appFeatureRepository.findByIdAndIsDeleted(id, 0)
                .map(appFeature -> toDto(appFeature, true))
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppFeatureDTO> getAllAppFeatures(Long tenantId) throws Exception {
        List<AppFeature> features = (tenantId != null)
                ? appFeatureRepository.findAllByTenantIdAndIsDeletedOrderBySequenceAsc(tenantId, 0)
                : appFeatureRepository.findAllByIsDeletedOrderBySequenceAsc(0);

        List<AppFeatureDTO> result = new ArrayList<>();
        for (AppFeature feature : features) {
            // Flat list; each DTO carries parentId so the client can build the tree without recursion.
            result.add(toDto(feature, false));
        }
        return result;
    }

    @Override
    @Transactional
    public AppFeatureDTO deleteAppFeature(Long id, Long deletedBy) throws Exception {
        Optional<AppFeature> optional = appFeatureRepository.findByIdAndIsDeleted(id, 0);
        if (optional.isEmpty()) {
            return null;
        }
        AppFeature entity = optional.get();
        entity.setIsDeleted(1);
        entity.setDeletedBy(deletedBy);
        entity.setDeletedTime(new Date());
        AppFeature saved = appFeatureRepository.save(entity);
        return toDto(saved, false);
    }

    // ---- Mapping (recursion-safe) ----

    /**
     * Map an entity to a DTO. {@code parentId} is always set; {@code includeRelations} additionally
     * populates a shallow {@code parent} DTO (scalar fields only, no further nesting) and a
     * one-level {@code childrens} list (each child carries parentId but no back-reference), so the
     * graph never cycles.
     */
    private AppFeatureDTO toDto(AppFeature e, boolean includeRelations) {
        if (e == null) {
            return null;
        }
        AppFeatureDTO dto = new AppFeatureDTO();
        dto.setId(e.getId());
        dto.setTenantId(e.getTenantId());
        dto.setFeatureName(e.getFeatureName());
        dto.setToolTip(e.getToolTip());
        dto.setSystemRole(e.getSystemRole());
        dto.setUrl(e.getUrl());
        dto.setSequence(e.getSequence());
        dto.setIsSystem(e.getIsSystem());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreationTime(e.getCreationTime());
        dto.setLastModifiedBy(e.getLastModifiedBy());
        dto.setLastModifiedTime(e.getLastModifiedTime());
        dto.setIsDeleted(e.getIsDeleted());
        dto.setDeletedBy(e.getDeletedBy());
        dto.setDeletedTime(e.getDeletedTime());

        AppFeature parent = e.getParent();
        dto.setParentId(parent != null ? parent.getId() : null);

        if (includeRelations) {
            if (parent != null) {
                dto.setParent(toShallowDto(parent));
            }
            if (e.getChildrens() != null && !e.getChildrens().isEmpty()) {
                List<AppFeatureDTO> children = new ArrayList<>();
                for (AppFeature child : e.getChildrens()) {
                    if (child.getIsDeleted() == 0) {
                        children.add(toShallowDto(child));
                    }
                }
                dto.setChildrens(children);
            }
        }
        return dto;
    }

    /** Scalar-only DTO (no parent/childrens) used for parent/child references to break the cycle. */
    private AppFeatureDTO toShallowDto(AppFeature e) {
        AppFeatureDTO dto = new AppFeatureDTO();
        dto.setId(e.getId());
        dto.setTenantId(e.getTenantId());
        dto.setFeatureName(e.getFeatureName());
        dto.setToolTip(e.getToolTip());
        dto.setSystemRole(e.getSystemRole());
        dto.setUrl(e.getUrl());
        dto.setSequence(e.getSequence());
        dto.setIsSystem(e.getIsSystem());
        dto.setParentId(e.getParent() != null ? e.getParent().getId() : null);
        return dto;
    }
}
