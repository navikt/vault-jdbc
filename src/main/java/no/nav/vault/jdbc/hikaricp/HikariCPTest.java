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
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/testdb");
        config.setMaxLifetime(5000);
        config.setMaximumPoolSize(1);
        config.setConnectionTimeout(250);
        config.setIdleTimeout(100);

        final HikariDataSource ds = HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, "testdb-user");

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

    private static void runQuery(HikariDataSource ds) {
        try {
            Connection conn = ds.getConnection();
            try {
                ResultSet res = conn.createStatement().executeQuery("select 42 as foo");
                res.next();
                int intRes = res.getInt("foo");
                System.out.println("intRes = " + intRes);
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
