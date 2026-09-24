package com.pcpl.carbon.playmobilservice.PlayMobil.Config;

import com.pcpl.carbon.pcplsdk.Common.Context.TenantContext;

import java.lang.reflect.Method;

/**
 * Write-side tenant stamping for the custom Players / PlayersCopyData save flows.
 *
 * <p>The SDK's generic {@code AbstractCRUDService} already stamps the tenant on entities it
 * saves (so SchemaMetadata, saved via {@code AbstractLazyService}, is covered), but the
 * PlayMobil player/copy-data services save their entities directly. This helper stamps the
 * current tenant (from {@link TenantContext}, i.e. the logged-in user's JWT) onto such an
 * entity before persisting, so the tenant is taken from the authenticated user and never from
 * the request body. No-op when there is no tenant in context (e.g. a super-admin request).
 */
public final class TenantSupport {

    private TenantSupport() {
    }

    public static <T> T stampTenant(T entity) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null || entity == null) {
            return entity;
        }
        try {
            Method setter = entity.getClass().getMethod("setTenantId", Long.class);
            setter.invoke(entity, tenantId);
        } catch (NoSuchMethodException ignored) {
            // entity is not tenant-scoped -> nothing to stamp
        } catch (Exception e) {
            throw new IllegalStateException("Unable to stamp tenant on " + entity.getClass().getSimpleName(), e);
        }
        return entity;
    }
}
