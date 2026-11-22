# NEXUS V6.1 — Requirement-to-source coverage

This matrix maps **implemented code** to course-project capabilities. It is not a claim of complete regression certification.

| Capability | Current source location | Evidence status |
| --- | --- | --- |
| Three roles and protected workflows | `domain/Account.java`, subtypes, `security/SecurityConfig.java`, `frontend/src/main.jsx` | Code inspected; fresh E2E verification outstanding |
| Incident creation and lifecycle | `web/IncidentController.java`, `service/IncidentService.java`, `domain/Incident.java` | Code-backed; fresh E2E outstanding |
| Priority calculation | `domain/Rules.java` | Earlier standalone tests recorded |
| Skill/load assignment with Strategy pattern | `strategy/`, `service/IncidentService.java` | Code-backed; earlier core tests recorded |
| SLA warnings, breaches, escalation | `service/SlaMonitor.java`, `IncidentService.java` | Code-backed; live timing tests outstanding |
| Observer events, audit and notifications | `event/`, `data/AuditRepository.java`, `data/NoticeRepository.java` | Code-backed; external delivery not assumed |
| Search, workload and charts | `web/IncidentController.java`, `frontend/src/command-v4.jsx` | UI + API code-backed |
| Campus location and map | `frontend/src/campus.jsx`, `domain/CampusCatalog.java`, `domain/GeoCheck.java` | Code-backed; site coordinates are pilot data |
| Duplicate suggestions and incident linking | `domain/Rules.java`, `service/IncidentService.java` | Code-backed; earlier similarity checks recorded |
| Service topology/decision explanation | `service/Infrastructure.java`, `CampusTopologyV6.java`, `web/AdminController.java` | Illustrative model, not live telemetry |
| Optional AI report assistant | `web/SmartReport.java` | Code-backed; requires explicit config for external AI |
| Theme centre and refreshed login | `frontend/src/appearance.jsx`, `login-refined.jsx`, `glass-v6.css` | Current UI source |
| Persistence | `data/` and `domain/` JPA entities | Code-backed; no current live DB certification here |
| Test artifacts | `tests/`, `backend/src/test/`, `TEST_RESULTS.md` | Historical 33 Java checks, not V6.1 E2E |

For a demo path, see [docs/DEMO.md](docs/DEMO.md). For what was actually tested, see [TEST_RESULTS.md](TEST_RESULTS.md).
