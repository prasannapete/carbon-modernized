package com.pcpl.carbon.authserver.AppFeature.Controller;

import com.pcpl.carbon.authserver.AppFeature.DTO.AppFeatureDTO;
import com.pcpl.carbon.authserver.AppFeature.Service.AppFeatureService;
import com.pcpl.carbon.authserver.Common.Response.ApplicationResponse;
import com.pcpl.carbon.authserver.Tenant.Service.TenantService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * CRUD endpoints for AppFeature. Tenant is resolved from the request (sub-domain) via the
 * authserver's existing {@link TenantService}; nothing about authentication is changed.
 */
@RestController
@RequestMapping("/app-feature")
@RequiredArgsConstructor
@Slf4j
public class AppFeatureController {

    private final AppFeatureService appFeatureService;
    private final TenantService tenantService;

    /** Create or update an AppFeature (update when the body carries an id). */
    @PostMapping("/save")
    public ApplicationResponse save(@RequestBody AppFeatureDTO appFeatureDTO,
                                    HttpServletRequest request) {
        try {
            Long tenantId = tenantService.resolveTenantId(request);
            AppFeatureDTO saved = appFeatureService.saveAppFeature(appFeatureDTO, tenantId);
            return ApplicationResponse.builder()
                    .success(true)
                    .message("AppFeature saved successfully")
                    .data(saved)
                    .build();
        } catch (Exception ex) {
            log.error("Failed to save AppFeature: {}", ex.getMessage(), ex);
            return ApplicationResponse.builder().success(false).error(ex.getMessage()).build();
        }
    }

    /** Get a single AppFeature by id. */
    @GetMapping("/get/{id}")
    public ApplicationResponse getById(@PathVariable Long id) {
        try {
            AppFeatureDTO dto = appFeatureService.getAppFeatureById(id);
            if (dto == null) {
                return ApplicationResponse.builder().success(false).message("AppFeature not found").build();
            }
            return ApplicationResponse.builder().success(true).data(dto).build();
        } catch (Exception ex) {
            log.error("Failed to get AppFeature {}: {}", id, ex.getMessage(), ex);
            return ApplicationResponse.builder().success(false).error(ex.getMessage()).build();
        }
    }

    /** Get all non-deleted AppFeatures (tenant-scoped when the request resolves a tenant). */
    @GetMapping("/get-all")
    public ApplicationResponse getAll(HttpServletRequest request) {
        try {
            Long tenantId = tenantService.resolveTenantId(request);
            return ApplicationResponse.builder()
                    .success(true)
                    .data(appFeatureService.getAllAppFeatures(tenantId))
                    .build();
        } catch (Exception ex) {
            log.error("Failed to get AppFeatures: {}", ex.getMessage(), ex);
            return ApplicationResponse.builder().success(false).error(ex.getMessage()).build();
        }
    }

    /** Soft-delete an AppFeature. */
    @PostMapping("/delete/{id}")
    public ApplicationResponse delete(@PathVariable Long id,
                                      @RequestParam(value = "deletedBy", required = false) Long deletedBy) {
        try {
            AppFeatureDTO deleted = appFeatureService.deleteAppFeature(id, deletedBy);
            if (deleted == null) {
                return ApplicationResponse.builder().success(false).message("AppFeature not found").build();
            }
            return ApplicationResponse.builder()
                    .success(true)
                    .message("AppFeature deleted successfully")
                    .data(deleted)
                    .build();
        } catch (Exception ex) {
            log.error("Failed to delete AppFeature {}: {}", id, ex.getMessage(), ex);
            return ApplicationResponse.builder().success(false).error(ex.getMessage()).build();
        }
    }
}
