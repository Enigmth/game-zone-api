package gamezone.common;

import gamezone.DataBase;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;

public final class FlywayMigrator {
    private FlywayMigrator() {
    }

    public static void migrate() {
        Flyway.configure()
                .dataSource(DataBase.getJdbcUrlForFlyway(), DataBase.getUserForFlyway(), DataBase.getPasswordForFlyway())
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion(MigrationVersion.fromVersion("0"))
                .load()
                .migrate();
    }
}
