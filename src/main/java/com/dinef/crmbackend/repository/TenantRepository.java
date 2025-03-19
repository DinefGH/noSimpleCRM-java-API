package com.dinef.crmbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dinef.crmbackend.entity.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Tenant findByIdentifier(String identifier);
}
