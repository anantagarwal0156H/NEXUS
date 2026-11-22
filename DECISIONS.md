# Engineering decisions

- Layered monolith keeps course OOP explicit and runs on one host. React handles display; Java is authoritative for actions and algorithms.
- Abstract Account with JPA single-table inheritance; role methods are overridden by Employee, Technician, Administrator. Incident state is guarded by methods. Other entity data uses simple public fields for readability; production refactor could encapsulate these further.
- Strategy pattern distinguishes skill-first vs least-load-first subject to qualification, availability and capacity. Stable ID tie-break. An assignment setting row is locked during assignment; admin availability edits are not globally serialized.
- Observer pattern uses synchronous Spring events inside the incident transaction for audit and persisted notices. Optional email delivery happens asynchronously from the outbox, separate from incident changes.
- Jaccard similarity threshold 0.35 after stop-word removal; same category/service, last 7 days, active and visible incidents. No automatic merging.
- SLA clock UTC, continuous elapsed time from creation, acknowledgment = response. Existing incident deadlines are snapshots; policy edits apply to new incidents. Warning/breach flags capture history; reopened incident retains original deadlines and prior breach history.
- Graph provider -> dependent; visited set BFS supports cycles; suspected root cause means coverage of at least two recently reported services, not verified outage or telemetry.
- PostgreSQL relational model; demo and production credentials kept in environment; secure-cookie mode for HTTPS. Basic session security and API authorization, not production certification.
- Hibernate auto-update chosen for time-limited initial build; migrate to Flyway, paging, stronger rate limits, secret rotation, deployment monitoring and integration tests before real operations.

## V6.1 implementation notes

- The current React UI includes a campus-facing Glass Operations login and configurable Theme Centre; these styling concerns do not own business rules.
- Location selection is assisted by browser geolocation only with a user action; server-side `GeoCheck` uses a designated pilot site radius and accuracy limits for selected resolution paths. The campus map is illustrative, not a live tracking product.
- An optional AI reporting endpoint uses explicit consent, conservative text checks, rate limits and a fallback to deterministic category suggestions. External AI is not required for regular reporting.
- The campus topology includes illustrative provider-to-dependent relationships that may be proposed separately from stored edges. It is not verified physical infrastructure data.
- Retained filenames such as `v2.jsx` and `command-v4.jsx` are still imported into the current frontend; version names are historical module labels and are not reasons to delete source files.
