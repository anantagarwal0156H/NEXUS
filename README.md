# NEXUS / Intelligent IT Operations

Java-first incident-management website for CS F213 / MAC F212. React + Vite, Spring Boot 3.4.5, Java 21, JPA, PostgreSQL, ECharts, React Flow. All dependencies are free/open source; no paid API required. This is an implementation candidate, not a verified cloud deployment. See STATUS.md.

## macOS local launch (recommended first)

1. Install Java 21, Maven and Node: `brew install openjdk@21 maven node`.
2. Extract the ZIP, open Terminal and `cd` into the NEXUS folder. Run `bash scripts/configure.sh`. In https://console.neon.tech/ open your existing project, select the production branch and Connect, choose direct (non-pooled) connection. Supply host, database name, role and password separately. Only use an empty/dedicated database with permission to create tables. Hibernate creates/updates tables on startup; do not point at a valuable database. The script stores secrets in a permission-restricted `.env` excluded from Git. Do not share it.
3. `bash scripts/build.sh` downloads frontend/Maven dependencies, builds the frontend into Spring Boot static resources, compiles backend and runs JUnit tests. `bash tests/run-core.sh` independently runs plain Java checks (set JAVA_HOME/PATH to Java 21 if necessary).
4. `bash scripts/start.sh` starts the combined site at http://localhost:8080. Open http://localhost:8080/health to check process readiness. Keep Terminal open; Ctrl+C stops it. Data stays in PostgreSQL.
5. Demo logins (all use the unique password entered in configure.sh): employee@nexus.demo, employee2@nexus.demo, tech@nexus.demo, tech2@nexus.demo, admin@nexus.demo. Seed creates these accounts, fictional services/categories and SLA settings. Report incidents to populate the charts. Existing account passwords are not reset by changing DEMO_PASSWORD.

For frontend development only, `cd frontend && npm run dev` with the backend already running on port 8080. Vite proxies API calls. For normal startup, use the combined site.

## Cloud deployment (after local verification)

Keep `.env` out of Git. Put these files at the root of your existing GitHub repository, commit after reviewing, and push. At https://dashboard.render.com/ create a Web Service linked to that repo, Docker runtime, free instance, root Dockerfile, health path `/health`. Add DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD through Environment (copy values, not shell `export`), COOKIE_SECURE=true, EMAIL_ENABLED=false. Use a dedicated empty database with NEXUS_DEMO=false and a strong ADMIN_PASSWORD to create admin@nexus.local; add technicians via the admin portal. For a separate fictional demo database only, set NEXUS_DEMO=true plus a unique DEMO_PASSWORD and do not distribute administrator credentials publicly. Public employee registration remains enabled; this is a class demo, not production-hardened. Free hosting may sleep and SLA background processing cannot run while asleep; deadlines use persisted timestamps and catch up when the service starts. Confirm free tier before provisioning; no billing or paid domain.

## Email

Default: simulated persistent outbox + in-app notifications. Optional real test delivery: set EMAIL_ENABLED=true, RESEND_API_KEY, EMAIL_FROM and EMAIL_TEST_RECIPIENT privately in `.env` or Render environment. Restart. Use only your own allowed test recipient (Resend may restrict onboarding@resend.dev). This version routes all external messages to that one configured test address; does not send to demo account addresses. SENT means provider HTTP acceptance, not inbox delivery. No live email was tested here.

See docs/DEMO.md, docs/requirements-matrix.md, docs/API.md, docs/architecture.md, TEAM_GUIDE.md and TEST_RESULTS.md. Keep secrets out of screenshots and logs.
