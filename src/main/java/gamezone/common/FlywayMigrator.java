package gamezone.common;

import gamezone.DataBase;
import org.flywaydb.core.Flyway;

public final class FlywayMigrator {
    private FlywayMigrator() {
    }

    public static void migrate() {
        Flyway.configure()
                .dataSource(DataBase.getJdbcUrlForFlyway(), DataBase.getUserForFlyway(), DataBase.getPasswordForFlyway())
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }
}
