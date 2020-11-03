[![Build Status](https://travis-ci.com/navikt/vault-jdbc.svg?branch=master)](https://travis-ci.com/navikt/vault-jdbc)
[![Published on Maven](https://img.shields.io/maven-metadata/v/https/repo1.maven.org/maven2/no/nav/vault-jdbc/maven-metadata.xml.svg)](https://repo1.maven.org/maven2/no/nav/vault-jdbc/)

# Hvordan få en applikasjon til å hente credentials fra Vault og koble seg til PostgreSQL

Dra inn vault-jdbc som avhengighet i pom.xml (hvis du bruker Maven), eller build.gradle (hvis du bruker Gradle).

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

## Feilsøking

### Bruk riktig version av HikariCP

Det kreves versjon 3.2.0 eller nyere. Kjør `mvn dependency:tree` (hvis du bruker Maven) for å dobbeltsjekke hvilken
versjon av HikariCP som brukes.

##### Det funger er i begynnelsen, men kræsjer etter ca. 1 time

Oppsettet er slik at vi bytter/roterer databasebruker og -passord en gang i timen. Hvis denne utskiftingen feiler, vil databasekoblingen slutte å fungere.

Dette kan f.eks. være fordi HikariCP har for gammel versjon, eller at appen ikke klarte å fornye Vault-tokenet sitt, slik at Vault-tokenet er ugyldig - da får den ikke lov til å hente database-credentials fra Vault.

