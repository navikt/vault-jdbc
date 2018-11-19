package no.nav.vault.jdbc;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.LogicalResponse;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;


public class VaultBasedConnectionPoolUtil {
    /*
    This function enriches a ComboPooledDataSource, by establishing a connection to Vault.
    It will ask Vault for database credentials, which will generate a new username/password with an expiration time.
    According to that expiration time, a Timer is set up to re-connect to Vault regularly to fetch a new
    username/password combination and update the DataSource with new credentials.
     */
    public static void setupAutoRefreshCredentialsFromVault(final ComboPooledDataSource cpds, final String role) throws Exception {
        final VaultConfig config =
                new VaultConfig()
                        .address("http://127.0.0.1:8200")               // Defaults to "VAULT_ADDR" environment variable
                        .token("123456789")                             // Defaults to "VAULT_TOKEN" environment variable
                        .openTimeout(5)                                 // Defaults to "VAULT_OPEN_TIMEOUT" environment variable
                        .readTimeout(30)                                // Defaults to "VAULT_READ_TIMEOUT" environment variable
                        .sslConfig(new SslConfig().build())             // See "SSL Config" section below
                        .build();

        final Vault vault = new Vault(config);

        final AtomicReference<String> username = new AtomicReference<String>();
        final AtomicReference<String> password = new AtomicReference<String>();
        final AtomicLong refreshInterval = new AtomicLong();

        final Timer timer = new Timer("VaultPollTimer", true);
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    final String path = "database/creds/" + role;
                    final LogicalResponse response = vault.logical().read(path);
                    username.set(response.getData().get("username"));
                    password.set(response.getData().get("password"));

                    cpds.setOverrideDefaultUser(username.get());
                    cpds.setOverrideDefaultPassword(password.get());
                    cpds.softResetAllUsers();
                    Connection conn = cpds.getConnection();
                    conn.close();

                    final Long duration = response.getLeaseDuration() * 1000;

                    long suggestedRefreshInterval;
                    if (duration < 60000) {
                        suggestedRefreshInterval = duration / 2;
                    } else {
                        suggestedRefreshInterval = duration - 30000;
                    }
                    refreshInterval.set(duration);
                    refreshInterval.set(suggestedRefreshInterval);
                    System.out.println("Renewed credentials (TTL=" + duration + "): " + username + " / " + password);
                } catch (VaultException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        task.run();

        timer.scheduleAtFixedRate(task, new Date(), refreshInterval.get());
    }
}
