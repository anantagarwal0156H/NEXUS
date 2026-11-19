# Requirements matrix — source coverage, not test certification

| Requirement | Implementation | Validation |
|---|---|---|
| Three roles, registration, login | AuthController, Accounts, SecurityConfig, role views | manual/API pending |
| Persistent CRUD/incident workflow | Incident, IncidentService, IncidentController | pure transition checks pass; integration pending |
| Priority | Rules.priority | pure checks pass |
| Two assignment strategies | strategy package, admin strategy | pure checks pass |
| SLA warnings/breaches | Rules.sla, SlaMonitor, IncidentService | timing checks pass; scheduled persistence pending |
| Two notifications, audit | event package, Notice/Audit | end-to-end pending |
| 5+ filters and reporting | IncidentController, ECharts | browser/API pending |
| Duplicate and escalation | Rules.similarity, IncidentService | similarity checks pass; workflow pending |
| Persistence | JPA PostgreSQL | Neon pending |
| OOP, Strategy, Observer | domain/strategy/event | explain source, test behaviors |
| Innovation | Infrastructure BFS, Decision Inspector, ECharts, replay v1 | BFS checks pass; actual UI pending |
| Testing, documentation | tests/, RulesTest, docs/ | 33 pure checks passed; JUnit/build and 3 workflows pending |
| Advanced simulation | not implemented | not applicable to v1 |
