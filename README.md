# Hvordan få en applikasjon til å hente credentials fra Vault og koble seg til PostgreSQL

Kopier/lim inn Java-klassene i dette repoet (bortsett fra `HikariCPTest`), inn i appen din.
(Koden er ikke publisert som at maven-artifakt ennå.)

For å opprette en DataSource, lager du en HikariConfig med konfigurasjon for appen,
og sender inn i HikariCPVaultUtil, som tar seg av kobling mot Vault.
Se `makeDataSource()` i `HikariCPTest`-klassen.

Denne DataSourcen bruker du videre i appen, for eksempel som en Spring Bean
(hvis du bruker Spring Framework).

### Hvordan kjøre eksempelkoden (HikariCPTest)

1) `docker-compose up` for å starte Vault og PostgreSQL lokalt
2) `./provision.sh` for å sette opp konfigurasjon
3) Kjør main-metoden med miljøvariabler: `VAULT_ADDR=http://localhost:8200` og `VAULT_TOKEN_PATH=vault_token.txt`

(Inni `vault_token.txt` ligger et hardkodet token, på nais/kubernetes vil det faktiske
tokenet injects inn i en fil på samme måte.)
