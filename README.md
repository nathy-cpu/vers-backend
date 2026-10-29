# VERS backend

REST API for the Vital Events Registration System: registrars record births,
deaths, marriages and divorces; officials and admins review them and generate
reports; admins manage users. Built with Quarkus, PostgreSQL and JWT auth.

## Setup

Requires JDK 21 and PostgreSQL.

1. Create the databases (defaults: user/password `quarkus`/`quarkus`, override
   with `DB_URL`, `DB_USER`, `DB_PASSWORD`; tests use `TEST_DB_URL`):

   ```shell script
   sudo -u postgres psql -c "CREATE ROLE quarkus LOGIN PASSWORD 'quarkus';" \
     -c "CREATE DATABASE quarkus OWNER quarkus;" \
     -c "CREATE DATABASE quarkus_test OWNER quarkus;"
   ```

2. Generate the JWT signing key pair. `privateKey.pem` is git-ignored; never commit it.

   ```shell script
   cd src/main/resources
   openssl genrsa -out rsa.pem 2048
   openssl pkcs8 -topk8 -nocrypt -in rsa.pem -out privateKey.pem
   openssl rsa -in rsa.pem -pubout -out publicKey.pem && rm rsa.pem
   ```

3. Run in dev mode on <http://localhost:8080>:

   ```shell script
   ./mvnw quarkus:dev
   ```

On startup, users `admin`/`adminpass`, `registrar`/`registrarpass` and
`official`/`officialpass` are created if missing. Change those passwords
before deploying anywhere real.

API docs: <http://localhost:8080/swagger-ui>. Tests (`./mvnw test`) recreate the
`quarkus_test` database on every run.

## API overview

| Path | Roles | Purpose |
|---|---|---|
| `POST /login` | anyone | Returns `{token, role, username}` |
| `GET /me`, `PUT /me/password` | any logged-in user | Own account |
| `/admin/...` | ADMIN | Add, list, search and remove users |
| `POST /registrar/register-event`, `GET/PUT /registrar/events[/{id}]` | REGISTRAR, ADMIN | Register, search and edit events |
| `GET /official/events`, `POST /official/events/{id}/approve\|reject` | OFFICIAL, ADMIN | Review events |
| `GET /official/reports`, `GET /official/reports/filters` | OFFICIAL, ADMIN | Statistics with optional filters |

New and edited events are `PENDING` until an official or admin approves or
rejects them. Nobody can review an event they registered.
