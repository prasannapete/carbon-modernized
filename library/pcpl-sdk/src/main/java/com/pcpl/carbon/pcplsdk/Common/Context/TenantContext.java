package com.pcpl.carbon.pcplsdk.Common.Context;

/**
 * Holds the current request's tenant id (resolved from the authenticated JWT by the
 * calling service, e.g. common-service's TenantFilter) for the duration of the request
 * thread. The generic CRUD layer reads this to scope reads/writes on a tenant basis.
 * A null value means "no tenant in context" and the CRUD layer then behaves as before.
 */
public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenantId(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
