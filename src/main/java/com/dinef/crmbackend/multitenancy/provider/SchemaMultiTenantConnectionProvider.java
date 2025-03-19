package com.dinef.crmbackend.multitenancy.provider;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.DatabaseConnectionInfo;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.spi.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dinef.crmbackend.multitenancy.TenantContext;

@Component
public class SchemaMultiTenantConnectionProvider
        implements MultiTenantConnectionProvider<String>, Stoppable {

    private static final Logger logger = LoggerFactory.getLogger(SchemaMultiTenantConnectionProvider.class);
    private final DataSource dataSource;

    @Autowired
    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Liefert eine Connection ohne tenant-spezifischen Wechsel.
    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    // Erzeugt eine Connection für den angegebenen Tenant, indem das Schema via setSchema() gewechselt wird.
    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        logger.info("Hole Verbindung für Tenant '{}'", tenantIdentifier);
        Connection connection = getAnyConnection();
        try {
            connection.setSchema(tenantIdentifier);
        } catch (SQLException e) {
            throw new SQLException("Schemawechsel zu [" + tenantIdentifier + "] fehlgeschlagen.", e);
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        logger.info("Gebe Verbindung für Tenant '{}' frei", tenantIdentifier);
        final String DEFAULT_TENANT = "public";
        try {
            connection.setSchema(DEFAULT_TENANT);
        } catch (SQLException e) {
            logger.error("Zurücksetzen des Schemas auf 'public' fehlgeschlagen", e);
        }
        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    // Rückgabe zusätzlicher Informationen zur Datenbankverbindung – hier delegiert an die Standardimplementierung.
    @Override
    public DatabaseConnectionInfo getDatabaseConnectionInfo(Dialect dialect) {
        return MultiTenantConnectionProvider.super.getDatabaseConnectionInfo(dialect);
    }

    // Unwrapping-Methoden für Hibernate
    @Override
    public <T> T unwrap(Class<T> clazz) {
        if (isUnwrappableAs(clazz)) {
            return clazz.cast(this);
        }
        throw new IllegalArgumentException("Unbekannter unwrap-Typ: " + clazz);
    }

    @Override
    public boolean isUnwrappableAs(Class<?> clazz) {
        return SchemaMultiTenantConnectionProvider.class.equals(clazz)
                || clazz.isAssignableFrom(getClass());
    }

    @Override
    public void stop() {
        // Optionaler Cleanup – hier nichts Spezielles erforderlich.
    }
}
