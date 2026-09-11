package com.pcpl.carbon.common_service.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registers the {@link TenantInterceptor} so tenant isolation applies to every request. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TenantInterceptor tenantInterceptor;

    public WebConfig(TenantInterceptor tenantInterceptor) {
        this.tenantInterceptor = tenantInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Run last so it executes after Open-Session-In-View has bound the EntityManager,
        // ensuring the Hibernate session is available to enable the tenant filter on.
        registry.addInterceptor(tenantInterceptor).order(Ordered.LOWEST_PRECEDENCE);
    }
}
