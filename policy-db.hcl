path "sys/renew/*" {
  capabilities = ["update"]
}

path "database/creds/testdb-user" {
  capabilities = ["read"]
}
