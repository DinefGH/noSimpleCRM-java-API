package com.dinef.crmbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.dinef.crmbackend.service.TenantService;

@Component
public class InitialTenantSetup implements CommandLineRunner {

    @Autowired
    private TenantService tenantService;

    @Override
    public void run(String... args) throws Exception {
        // Erstelle die ersten zwei Tenants, falls sie noch nicht existieren
        tenantService.createTenant("dinef_corp", "Dinef CORP.");
        tenantService.createTenant("nexon", "Nexon");
    }
}
