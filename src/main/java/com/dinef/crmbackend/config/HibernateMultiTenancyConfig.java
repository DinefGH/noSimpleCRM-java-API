package com.dinef.crmbackend.config;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import com.dinef.crmbackend.multitenancy.provider.SchemaMultiTenantConnectionProvider;
import com.dinef.crmbackend.multitenancy.resolver.SchemaCurrentTenantIdentifierResolver;

@Configuration
public class HibernateMultiTenancyConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SchemaMultiTenantConnectionProvider multiTenantConnectionProvider;

    @Autowired
    private SchemaCurrentTenantIdentifierResolver tenantIdentifierResolver;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        // Passe den Package-Namen an, in dem deine JPA-Entities liegen
        em.setPackagesToScan("com.dinef.crmbackend.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        // Entferne die Zeile mit MULTI_TENANT und MultiTenancyStrategy – Hibernate erkennt Multi-Tenancy jetzt automatisch.
        // properties.put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        properties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        properties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
        properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        properties.put(AvailableSettings.HBM2DDL_AUTO, "update");

        em.setJpaPropertyMap(properties);
        return em;
    }
}
