path "sys/renew/*" {
  capabilities = ["update"]
}

path "postgresql/preprod/creds/testdb-user" {
  capabilities = ["read"]
}
