package com.dinef.crmbackend.multitenancy.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.dinef.crmbackend.multitenancy.TenantContext;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Lese den Tenant aus dem HTTP-Header "X-TenantID"
        String tenant = request.getHeader("X-TenantID");
        if (tenant != null) {
            TenantContext.setCurrentTenant(tenant);
        } else {
            // Fallback: Standardschema "public"
            TenantContext.setCurrentTenant("public");
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
