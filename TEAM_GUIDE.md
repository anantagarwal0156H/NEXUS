# NEXUS V6.1 — OOP viva and code map

## 30-second explanation

NEXUS is a Java-centered campus incident-management system. Students report problems, the backend assigns technicians using configurable strategies, technicians resolve cases, and administrators oversee workload, SLA compliance and campus service dependencies. React displays the workspaces; **Java owns the business decisions**.

## Follow a ticket through the code

1. `web/IncidentController.java` receives a report and validates its request.
2. `service/IncidentService.java` creates the incident, calculates priority and runs assignment.
3. `domain/Incident.java` applies guarded state changes; `domain/Rules.java` provides pure rules such as priority, SLA and similarity.
4. `strategy/AssignmentStrategy.java` defines the common contract; `SkillBasedAssignment.java` and `LoadBasedAssignment.java` implement different rankings.
5. `event/IncidentEvent.java` and `event/EventListeners.java` fan out audit and notification actions.
6. `data/*Repository.java` persists entities through Spring Data JPA.
7. React consumes the APIs in `frontend/src/main.jsx` and related view modules.

## Show the OOP concepts with specific classes

| Concept | Evidence | Viva explanation |
| --- | --- | --- |
| Abstraction | `domain/Account.java` | Shared account properties and a common role contract |
| Inheritance | `Employee`, `Technician`, `Administrator` | Specialized account types |
| Polymorphism | `AssignmentStrategy` | Same decision interface, different runtime implementations |
| Encapsulation | `Incident.acknowledge`, `resolve`, `verify` and `close` | State transitions are checked in domain methods |
| Observer | `IncidentEvent` + listeners | Audit and notice behavior reacts to an incident event |
| Repository | `data/` interfaces | Persistence separated from business service logic |
| Separation of concerns | `web/`, `service/`, `domain/`, `data/` | Controllers do not determine technician selection |

## Newer campus-facing implementation

- `frontend/src/login-refined.jsx`: V6 campus-facing sign-in.
- `frontend/src/appearance.jsx`: Theme Centre, glass presets and personalization.
- `frontend/src/campus.jsx`: campus map, site picker, browser-side location suggestion.
- `domain/GeoCheck.java` and `web/IncidentController.java`: server-side pilot position checks for certain resolution operations.
- `web/SmartReport.java`: opt-in AI-assisted report suggestions with local fallback. **Do not describe AI as essential or always enabled.**
- `service/CampusTopologyV6.java`, `web/TopologyV6Controller.java`: optional illustrative dependency links; the admin endpoint is access controlled.
- `frontend/src/command-v4.jsx`: current operations dashboards and topology visualization.
- `web/AdminController.java`: admin controls, assignment-strategy changes and audit APIs.

Older filenames like `v2.jsx` are retained **because current code still imports and uses components from them**. Do not delete files based on version-like filenames.

## What not to claim

The application does not ingest live campus infrastructure telemetry. Historical replay does not reproduce the entire campus at an arbitrary time. A successful historical standalone Java check is not proof that V6.1 is fully regression-tested. A user-facing location suggestion is not guaranteed exact GPS tracking.

## Technical tradeoffs

- Skill/load ranking: candidate sorting is approximately O(T log T).
- Graph traversal: BFS is O(V + E).
- Similarity: token-set comparison is intended as a lightweight suggestion, not a semantic duplicate guarantee.
- Some reporting and workload paths scan incident records, suitable for demonstration but candidates for indexed and paginated queries at scale.

Use [architecture diagrams](docs/architecture.md) and [demo sequence](docs/DEMO.md) alongside this guide.
