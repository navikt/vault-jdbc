# Greie for å integrere Vault og JDBC

Måter å gjøre det på:

1) Alltid bare restarte appen

2) Polle rett mot Vault, gitt et vault-token, for å hente passord og oppdatere

3) Polle fra en fil som ligger på disk, som endres en gang i blant
