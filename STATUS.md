# NEXUS — current implementation status

**Documentation baseline:** 22 November 2026. This page describes source on `main`, not an independent live-production audit.

## Version provenance

- **20 Nov 2026:** commit `81e886e` — **NEXUS V6.1 – Glass Operations Release**.
- **21 Nov 2026:** `d6b37e8` — landing-page overlap fix.
- **21 Nov 2026:** `a8ee600` — separate admin Incident Centre from SLA Monitor.
- **22 Nov 2026:** README-only changes. No later application-code release was found in the inspected history.

Thus the latest recognizable *named* release is **V6.1**, with post-release refinements. No GitHub Release/tag establishes a different version number.

## Present in current source

- Spring Boot API, JPA PostgreSQL model, session authentication, CSRF, and role-scoped access.
- Student/employee, technician, and administrator workspaces; campus-oriented login and glass-style theme centre.
- Incident creation, classification, priority, skill/load assignment, lifecycle, communications and audit.
- Campus location picker and Leaflet map with OpenStreetMap tiles; GPS suggestions and restricted pilot location verification for selected resolution actions.
- SLA processing, admin configuration, warnings/breaches, dashboard analytics, Incident Centre, topology explorer and assignment Decision Inspector.
- Persistent in-app notifications and simulated email outbox; optional restricted external email integration.
- Optional smart-report endpoint with local rule fallback when Groq is disabled or unavailable.

## Evidence: distinguish implementation from validation

The historical `TEST_RESULTS.md` documents **33 standalone Java checks passing in an earlier environment**. That report did not verify V6.1 as deployed, so a fresh build and end-to-end regression pass should be recorded before claiming certification.

The user reports a live Render deployment; this repository alone does not expose a verified live URL, uptime measurements, monitoring-provider configuration, or complete hosted-test logs.

## Known limitations / intentional boundaries

- Illustrative topology and campus reports are **not** live device/network telemetry.
- Ticket replay is based on audit history, not a full historical graph reconstruction.
- Geolocation is optional for reporting; verification is only applied to location-identified resolution flows and depends on reference site coordinates and device accuracy.
- Optional AI depends on separate private environment configuration and consent; local rules remain the fallback.
- Free-tier hosting can cold-start; external requests do not guarantee uptime.
- Database schema currently relies on Hibernate updating tables rather than versioned migrations. Production security, load tests, and data-retention policies need further work.

See [README](README.md), [feature coverage](RUBRIC_COVERAGE.md), [test evidence](TEST_RESULTS.md), and [architecture](docs/architecture.md).
