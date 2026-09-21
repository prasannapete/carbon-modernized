package com.pcpl.carbon.kitkat_service.Config;

import com.pcpl.carbon.pcplsdk.Common.Context.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Enforces tenant isolation for every request:
 * <ul>
 *   <li>resolves the tenant from the authenticated JWT's {@code tenantId} claim into
 *       {@link TenantContext} (used to stamp the tenant on writes, never trusting the
 *       request body), and</li>
 *   <li>enables the reusable Hibernate {@code tenantFilter} on the current session so all
 *       reads of tenant-aware entities are automatically scoped to that tenant.</li>
 * </ul>
 * A user with no tenant (tenant_id null, e.g. a super-admin) carries no {@code tenantId}
 * claim; the filter is then left disabled so the request can see every tenant's data.
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        Long tenantId = resolveTenantId();
        TenantContext.setTenantId(tenantId);
        // A user with a tenant -> scope every read to that tenant.
        // A user with no tenant (tenant_id null, e.g. super-admin) -> leave the filter
        // disabled so data across all tenants is visible.
        if (tenantId != null) {
            entityManager.unwrap(Session.class)
                    .enableFilter("tenantFilter")
                    .setParameter("tenantId", tenantId);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        TenantContext.clear();
    }

    /** The logged-in user's tenant id from the JWT, or {@code null} when absent/invalid. */
    private Long resolveTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt
                && jwt.getClaim("tenantId") instanceof Number number) {
            return number.longValue();
        }
        return null;
    }
}
