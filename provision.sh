#!/bin/bash
export VAULT_ADDR=http://localhost:8200
export VAULT_TOKEN=123456789

export PGSQL_ROOT_USERNAME=postgres # Default from the PostgreSQL docker image
export PGPASSWORD=vaultpassword # Password set up in docker-compose.yml
export PGSQL_HOST=testdb # Host name/service name set up in docker-compose.yml

echo "Bootstrapping PostgreSQL"
psql_root() {
  psql --host=localhost -U $PGSQL_ROOT_USERNAME "$@"
}

# Create a template database; the only important thing is that "database-iac" must own the "public" schema!
# That way, access to CREATE in "public" can be revoked later on, without superuser access.
psql_root -c 'CREATE DATABASE "testdb"'

vault secrets enable -path=postgresql/preprod database
vault write postgresql/preprod/config/testdb \
  allowed_roles="testdb-user" \
  plugin_name=postgresql-database-plugin \
  connection_url="postgresql://{{username}}:{{password}}@$PGSQL_HOST:5432/testdb?sslmode=disable" \
  username="$PGSQL_ROOT_USERNAME" \
  password="$PGPASSWORD"

vault write postgresql/preprod/roles/testdb-user \
    db_name=testdb \
    creation_statements="CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}'" \
    default_ttl="1m" \
    max_ttl="1m"

vault policy write db policy-db.hcl

vault token create -policy=db -ttl=768h -period=15s -field token
