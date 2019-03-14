package no.nav.vault.jdbc.hikaricp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class HikariCPTest {
    public static void main(String[] args) throws Exception {
        final HikariDataSource ds = makeDataSource();

        // The rest of the code here is just testing that the data source works.
        final Timer timer = new Timer("PostgreSQLSelectTimer", true);
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runQuery(ds);
            }
        };
        timer.scheduleAtFixedRate(task, new Date(), 1000);

        System.out.println("Running SELECT statements for an hour...");
        Thread.sleep(3600000);
        System.out.println("Done");
    }

    private static HikariDataSource makeDataSource() throws VaultError {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/testdb");
        config.setMinimumIdle(0);
        config.setMaxLifetime(30001);
        config.setMaximumPoolSize(2);
        config.setConnectionTimeout(250);
        config.setIdleTimeout(10001);
        return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, "postgresql/preprod", "testdb-user");
    }

    private static void runQuery(HikariDataSource ds) {
        try {
            Connection conn = ds.getConnection();
            try {
                ResultSet res = conn.createStatement().executeQuery("select 42 as foo");
                res.next();
                int intRes = res.getInt("foo");
                System.out.println("username = " + conn.getMetaData().getUserName() + ", intRes = " + intRes);
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
