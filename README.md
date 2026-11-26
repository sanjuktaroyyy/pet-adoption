# Pet Adoption Management System (Java AWT + MySQL/JDBC)

Package `petadoption` (model, service, exception, db, util, gui). Features: register/login,
add/search/display pets, adoption queue processed by a thread, statistics via Streams.

## Run locally
1. Install JDK 17+, Maven, MySQL 8.
2. `mysql -u root -p < sql/schema.sql`  (creates database `petdb` and tables)
3. Optional env vars: `DB_URL`, `DB_USER`, `DB_PASS` (defaults: localhost:3306/petdb, root/root).
4. `mvn compile exec:java -Dexec.mainClass=petadoption.Main`
   (also: `petadoption.gui.SimpleAWTExample` for the basic AWT demo)

## GitHub
Each push runs `.github/workflows/ci.yml`: starts MySQL, loads `sql/schema.sql`, compiles and
runs `petadoption.SmokeTest` (JDBC, validation, queue, thread life cycle, streams).
GitHub runners have no display, so the AWT windows only open on your own machine.
