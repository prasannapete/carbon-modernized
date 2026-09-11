package com.pcpl.carbon.authserver.Tenant.Service;

import com.pcpl.carbon.authserver.Tenant.Model.Tenant;
import com.pcpl.carbon.authserver.Tenant.Repository.TenantRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Resolves the tenant for an incoming request from the request host's sub-domain
 * and maps it to the tenant-specific login screen. Falls back to the default login
 * screen whenever no tenant matches or the tenant template is missing.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TenantService {

    /** Default login view used when no tenant-specific screen applies. */
    public static final String DEFAULT_LOGIN_TEMPLATE = "login/login-page";

    private final TenantRepository tenantRepository;
    private final ResourceLoader resourceLoader;

    @Value("${spring.thymeleaf.prefix:classpath:/templates/}")
    private String templatePrefix;

    @Value("${spring.thymeleaf.suffix:.html}")
    private String templateSuffix;

    /**
     * Extract the tenant sub-domain (first host label) from the request.
     * Returns {@code null} for hosts without a tenant sub-domain (localhost, IPs,
     * bare two-label domains and "www").
     */
    public String resolveSubdomain(HttpServletRequest request) {
        String host = request.getServerName();
        if (host == null || host.isBlank()) {
            return null;
        }
        host = host.trim().toLowerCase();
        if (host.equals("localhost") || host.matches("^[0-9.]+$")) {
            return null;
        }
        String[] labels = host.split("\\.");
        // Need at least sub.domain.tld for a sub-domain to be present.
        if (labels.length < 3) {
            return null;
        }
        String subdomain = labels[0];
        if (subdomain.equals("www")) {
            return null;
        }
        return subdomain;
    }

    /**
     * Resolve the login view name for the current request, falling back to
     * {@link #DEFAULT_LOGIN_TEMPLATE} when no tenant screen applies.
     *
     * <p>The request sub-domain must match an active {@code cb_tenant} row
     * (by {@code tenant_name}); the sub-domain is then used as the login-screen
     * folder name, e.g. {@code acme} resolves to {@code login/acme/login-page}.
     */
    public String resolveLoginTemplate(HttpServletRequest request) {
        String subdomain = resolveSubdomain(request);
        if (subdomain == null) {
            return DEFAULT_LOGIN_TEMPLATE;
        }
        try {
            Optional<Tenant> tenant = tenantRepository.findByTenantNameIgnoreCaseAndIsDeleted(subdomain, 0);
            if (tenant.isPresent()) {
                String candidate = "login/" + subdomain + "/login-page";
                if (templateExists(candidate)) {
                    return candidate;
                }
                log.warn("Login template '{}' for tenant '{}' not found; using default login screen.",
                        candidate, subdomain);
            }
        } catch (Exception ex) {
            // e.g. cb_tenant table not reachable - keep the default screen working.
            log.warn("Tenant lookup failed for sub-domain '{}': {}. Using default login screen.",
                    subdomain, ex.getMessage());
        }
        return DEFAULT_LOGIN_TEMPLATE;
    }

    private boolean templateExists(String viewName) {
        try {
            return resourceLoader.getResource(templatePrefix + viewName + templateSuffix).exists();
        } catch (Exception ex) {
            return false;
        }
    }
}
