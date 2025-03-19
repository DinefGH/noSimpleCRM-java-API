package com.dinef.crmbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dinef.crmbackend.entity.Tenant;
import com.dinef.crmbackend.repository.TenantRepository;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate; // Wird genutzt, um direkt SQL-Befehle auszuführen

    /**
     * Lege einen neuen Tenant an und erstelle das entsprechende Schema.
     */
    @Transactional
    public Tenant createTenant(String identifier, String name) {
        // 1. Speichere Tenant-Daten in der zentralen Tabelle
        Tenant tenant = new Tenant();
        tenant.setIdentifier(identifier);
        tenant.setName(name);
        tenant = tenantRepository.save(tenant);

        // 2. Erstelle das Schema in PostgreSQL (Identifier muss in SQL-sicherem Format vorliegen)
        String sql = "CREATE SCHEMA IF NOT EXISTS " + identifier;
        jdbcTemplate.execute(sql);

        // Optional: Hier können weitere Initialisierungen erfolgen, z.B. Tabellen im neuen Schema erstellen.
        return tenant;
    }
}
