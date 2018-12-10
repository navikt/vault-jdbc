# Hvordan få en applikasjon til å hente credentials fra Vault og koble seg til PostgreSQL

Kopier/lim inn Java-klassene i dette repoet, inn i appen din.
(Koden er ikke publisert som at maven-artifakt ennå.)

For å opprette en DataSource, lager du en HikariConfig med konfigurasjon for appen,
og sender inn i HikariCPVaultUtil, som tar seg av kobling mot Vault.
