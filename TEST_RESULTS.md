# Test evidence

Executed in development container: OpenJDK 21, `bash tests/run-core.sh`: 33 checks passed. See `tests/CoreChecks.java` and `docs/core-test-output.txt` for exact cases and output. Maven JUnit tests supplied, not run here. No claim of PostgreSQL, UI or deployment success.

## Additional acceptance tests (pending)

| ID | Test | Expected |
|---|---|---|
| A1 | Register valid employee and login | employee session |
| A2 | Incorrect password | 401 |
| A3 | Employee calls admin API | 403 |
| A4 | Other employee opens ticket ID | 403 |
| A5 | Create with missing title | 400 |
| A6 | Valid create, then restart | same incident persisted |
| A7 | No eligible technician | CLASSIFIED, admin alerted |
| A8 | Technician attempts resolve before start | 400 |
| A9 | Wrong technician resolves | 403 |
| A10 | Complete lifecycle | CLOSED + persistent audit |
| A11 | Private note then employee reads | note excluded |
| A12 | SLA deadline passes | one breach and escalation |
| A13 | Notifications | IN_APP and SIMULATED EMAIL records |
| A14 | Filter by status, priority, service, category, date | expected scoped records |
| A15 | Duplicate link | related ID persisted |
| A16 | Administrator changes strategy | next auto-assignment uses it |
| A17 | Graph selects node | upstream/downstream from Java |
| A18 | Replay steps | state from audit history |
| A19 | Reports counts | reconcile stored incidents |
| A20 | Resend authorized test | provider acceptance or FAILED; no loss of ticket |

Record actual observations and evidence after execution, not before.
