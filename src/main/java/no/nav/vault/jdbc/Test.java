package no.nav.vault.jdbc;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

class Test {
    public static void main(String[] args) throws Exception {
        System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF");

        final ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("org.postgresql.Driver");
        cpds.setJdbcUrl("jdbc:postgresql://localhost:5432/testdb");
        cpds.setMaxPoolSize(5);
        cpds.setMaxConnectionAge(15);

        VaultBasedConnectionPoolUtil.setupAutoRefreshCredentialsFromVault(cpds, "testdb-user");

        final Timer timer2 = new Timer("PostgreSQLSelectTimer", true);
        final TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                runQuery(cpds);
            }
        };
        timer2.scheduleAtFixedRate(task2, new Date(), 1000);

        System.out.println("Doing stuff");
        Thread.sleep(30000);
        System.out.println("Done");
    }

    private static void runQuery(DataSource cpds) {
        try {
            Connection conn = cpds.getConnection();
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
